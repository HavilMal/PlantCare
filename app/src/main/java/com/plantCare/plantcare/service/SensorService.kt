package com.plantCare.plantcare.service

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.util.size
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class SensorData(
    val humidity: Float,
    val light: Float,
)

val SENSOR_SERVICE_UUID = ParcelUuid.fromString("ca658d05-b9dc-4350-bc8e-a0004b52cd8f")
val HUMIDITY_UUID = ParcelUuid.fromString("d3556f5b-d6e1-496e-849c-d0ed453c9e5f")
val LIGHT_UUID = ParcelUuid.fromString("0836eae8-5ae5-4d2b-a2f9-3984fd34562c")

// notification uuid
val CCC_DESCRIPTOR_UUID = ParcelUuid.fromString("00002902-0000-1000-8000-00805f9b34fb")

class SensorScanCallback(
    private val onDeviceFound: (BluetoothDevice) -> Unit,
    private val onDeviceNotFound: () -> Unit,
) : ScanCallback() {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        val deviceName = result?.device?.name ?: "Unknown Device"
        val deviceAddress = result?.device?.address ?: "00:00:00:00:00:00"

        Log.d("sensorScan", "Found: $deviceName ($deviceAddress)")
        val manufacturerData = result?.scanRecord?.manufacturerSpecificData

        if (manufacturerData == null) {
            return
        }

        // A device can technically have multiple manufacturer data entries
        for (i in 0 until manufacturerData.size) {
            val id = manufacturerData.keyAt(i)
            val data = manufacturerData.valueAt(i)

            // Example: If ID is 0xFFFF and data is "HI" (48 49), show a message
            if (id == 0xFFFF && data.contentEquals(byteArrayOf(0x48, 0x49))) {
                onDeviceFound(result.device!!)
            }
        }
    }

    override fun onScanFailed(errorCode: Int) {
        Log.e("sensorScan", "Scan failed with error code: $errorCode")
        onDeviceNotFound()
    }
}

class SensorDataCallback(
    private val onHumidityUpdate: (Float) -> Unit,
    private val onLightUpdate: (Float) -> Unit,
    private val onConnectionFailed: () -> Unit,
) : BluetoothGattCallback() {
    private val characteristicsToSubscribe =
        mutableListOf<UUID>(HUMIDITY_UUID.uuid, LIGHT_UUID.uuid)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        Log.w("sensorScan", "Connection status: $status")
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.w("sensorScan", "sensor not available")
            gatt.close()
            onConnectionFailed()
        }

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            subscribeToNext(gatt)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun subscribeToNext(gatt: BluetoothGatt) {
        if (characteristicsToSubscribe.isEmpty()) return

        val charUuid = characteristicsToSubscribe.removeAt(0)
        val service = gatt.getService(SENSOR_SERVICE_UUID.uuid)
        val characteristic = service?.getCharacteristic(charUuid)

        characteristic?.let { char ->
            gatt.setCharacteristicNotification(char, true)
            val descriptor = char.getDescriptor(CCC_DESCRIPTOR_UUID.uuid)
            descriptor?.let {
                gatt.writeDescriptor(it, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            subscribeToNext(gatt)
        } else {
            Log.e("sensorScan", "Failed to write descriptor: $status")
        }
    }


    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        when (characteristic.uuid) {
            HUMIDITY_UUID.uuid -> {
                val value = value.decodeToString().toFloat()
                onHumidityUpdate(value)
            }

            LIGHT_UUID.uuid -> {
                val value = value.decodeToString().toFloat()
                onLightUpdate(value)
            }
        }
    }
}

class SensorService(val context: Context) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner
    val manufacturerId = 0xFFFF
    val manufacturerData = byteArrayOf(0x48.toByte(), 0x49.toByte())
    private val filter = ScanFilter.Builder()
        .setManufacturerData(manufacturerId, manufacturerData)
        .setServiceUuid(SENSOR_SERVICE_UUID)
        .build()

    private val settings =
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .build()


    private var scanning: Boolean = false

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForSensor(): Flow<BluetoothDevice?> = callbackFlow {
        val callback = SensorScanCallback(
            onDeviceFound = {
                trySend(it)
            },
            onDeviceNotFound = { close() }
        )

        scanning = true
        scanner.startScan(
            listOf(filter),
            settings,
            callback,
        )

        val timeout = launch {
            delay(60 * 1000)
            close()
        }

        awaitClose {
            timeout.cancel()
            scanner.stopScan(callback)
            scanning = false
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getSensorDataFlow(address: String): Flow<SensorData?> = callbackFlow {
        var currentState = SensorData(0F, 0F)
        var gatt: BluetoothGatt? = null

        fun connect() {
            lateinit var device: BluetoothDevice
            try {
                device = bluetoothAdapter.getRemoteDevice(address)
            } catch (exception: IllegalArgumentException) {
                trySend(null)
                close(exception)
            }

            gatt = device.connectGatt(
                context, false, SensorDataCallback(
                    onLightUpdate = {
                        currentState = currentState.copy(light = it)
                        trySend(currentState)
                    },
                    onHumidityUpdate = {
                        currentState = currentState.copy(humidity = it)
                        trySend(currentState)
                    },
                    onConnectionFailed = {
                        launch {
                            delay(5000)
                            connect()
                        }
                    }
                )
            )
        }

        connect()

        awaitClose {
            gatt?.disconnect()
            gatt?.close()
        }
    }

    fun getBluetoothStateFlow(): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    trySend(state == BluetoothAdapter.STATE_ON)
                }
            }
        }

        // Register the receiver
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

        // Send the initial state immediately
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val state = bluetoothManager.adapter?.state
        trySend(state == BluetoothAdapter.STATE_ON)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }


}