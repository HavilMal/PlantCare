package com.plantCare.plantcare.service

import android.Manifest
import android.annotation.SuppressLint
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
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.util.size
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

data class SensorData(
    val humidity: Float,
    val light: Float,
)

val SENSOR_SERVICE_UUID = ParcelUuid.fromString("12345678-1234-1234-1234-1234567890ab")
val HUMIDITY_UUID = ParcelUuid.fromString("abcdefab-1234-5678-1234-abcdefabcdef")
// notification uuid
val CCC_DESCRIPTOR_UUID = ParcelUuid.fromString("00002902-0000-1000-8000-00805f9b34fb")

class SensorScanCallback(
    private val onDeviceFound: (BluetoothDevice) -> Unit,
    private val onDeviceNotFound: () -> Unit,
) : ScanCallback() {
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
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
) : BluetoothGattCallback() {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            val service = gatt.getService(SENSOR_SERVICE_UUID.uuid)
            val characteristic = service?.getCharacteristic(HUMIDITY_UUID.uuid)

            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic?.getDescriptor(CCC_DESCRIPTOR_UUID.uuid) ?: return
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)

        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        when (characteristic.uuid) {
            HUMIDITY_UUID.uuid -> {
                Log.d("sensorData", "received data: ${value.decodeToString()}")
                val value = value.decodeToString().toFloat()
                onHumidityUpdate(value)
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

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
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
        lateinit var device: BluetoothDevice
        try {
            Log.d("sensorScan", "Establishing connection to: $address")
            device = bluetoothAdapter.getRemoteDevice(address)
            Log.d("sensorScan", "connected to: $address")
        } catch (exception: IllegalArgumentException) {
            Log.w("sensorScan", "Device not found with provided address.")
            trySend(null)
            close()
        }

        val gatt = device.connectGatt(
            context, false, SensorDataCallback(
                onLightUpdate = {
                    currentState = currentState.copy(light = it)
                    trySend(currentState)
                },
                onHumidityUpdate = {
                    currentState = currentState.copy(humidity = it)
                    trySend(currentState)
                },
            )
        )

        awaitClose {
            gatt.disconnect()
            gatt.close()
        }
    }
}