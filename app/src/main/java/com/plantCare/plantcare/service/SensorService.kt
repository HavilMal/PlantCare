package com.plantCare.plantcare.service

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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
    val light: Boolean,
)

val SENSOR_SERVICE_UUID = ParcelUuid.fromString("12345678-1234-1234-1234-1234567890ab")

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

    fun getSensorData(id: Int): SensorData? {

        return null
    }
}