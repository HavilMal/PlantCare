package com.plantCare.plantcare

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.cash.turbine.test
import com.plantCare.plantcare.service.* // Make sure this matches your package
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SensorServiceTest {
    @MockK(relaxed = true)
    lateinit var bluetoothManager: BluetoothManager

    @MockK
    lateinit var bluetoothAdapter: BluetoothAdapter

    @MockK(relaxed = true)
    lateinit var scanner: BluetoothLeScanner

    @MockK(relaxed = true)
    lateinit var device: BluetoothDevice

    @MockK(relaxed = true)
    lateinit var gatt: BluetoothGatt

    @MockK(relaxed = true)
    lateinit var context: Context

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var sensorService: SensorService

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { context.getSystemService(Context.BLUETOOTH_SERVICE) } returns bluetoothManager
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { bluetoothAdapter.bluetoothLeScanner } returns scanner
        every { bluetoothAdapter.state } returns BluetoothAdapter.STATE_ON
        every { bluetoothAdapter.getRemoteDevice(any<String>()) } returns device

        sensorService = SensorService(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `scanForSensor emits device when valid scan result is found`() = runTest {
        val callbackSlot = slot<ScanCallback>()

        every {
            scanner.startScan(any(), any(), capture(callbackSlot))
        } just Runs
        every { scanner.stopScan(any<ScanCallback>()) } just Runs

        val mockScanResult = mockk<ScanResult>(relaxed = true)
        val mockScanRecord = mockk<ScanRecord>(relaxed = true)

        every { mockScanResult.device } returns device
        every { mockScanResult.scanRecord } returns mockScanRecord
        every { mockScanRecord.manufacturerSpecificData } returns mockk(relaxed = true)

        // test callback when device is found
        sensorService.scanForSensor().test {
            callbackSlot.captured.onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult)

            val emittedDevice = awaitItem()
            assertEquals(device, emittedDevice)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getSensorDataFlow emits updated data on characteristic change`() = runTest {
        val callbackSlot = slot<BluetoothGattCallback>()

        val humidityChar = BluetoothGattCharacteristic(
            HUMIDITY_UUID.uuid,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        val lightChar = BluetoothGattCharacteristic(
            LIGHT_UUID.uuid,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        val mockService = mockk<BluetoothGattService>(relaxed = true)
        every { device.connectGatt(any(), any(), capture(callbackSlot)) } returns gatt
        every { gatt.getService(SENSOR_SERVICE_UUID.uuid) } returns mockService
        every { mockService.getCharacteristic(HUMIDITY_UUID.uuid) } returns humidityChar
        every { mockService.getCharacteristic(LIGHT_UUID.uuid) } returns lightChar

        sensorService.getSensorDataFlow("00:11:22:33:44:55").test {
            callbackSlot.captured.onConnectionStateChange(gatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED)
            callbackSlot.captured.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS)

            val humidityValue = "45.5".toByteArray()
            callbackSlot.captured.onCharacteristicChanged(gatt, humidityChar, humidityValue)

            val item1 = awaitItem()
            assertEquals(45.5f, item1?.humidity)

            val lightValue = "120.0".toByteArray()
            callbackSlot.captured.onCharacteristicChanged(gatt, lightChar, lightValue)

            val item2 = awaitItem()
            assertEquals(120.0f, item2?.light)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBluetoothStateFlow emits true when broadcast received`() = runTest {
        val receiverSlot = slot<BroadcastReceiver>()

        every {
            context.registerReceiver(capture(receiverSlot), any())
        } returns mockk()

        every { context.unregisterReceiver(any()) } just Runs

        sensorService.getBluetoothStateFlow().test {
            assertEquals(true, awaitItem())

            val intentOff = Intent(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
                putExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            }

            receiverSlot.captured.onReceive(context, intentOff)
            assertEquals(false, awaitItem())

            val intentOn = Intent(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
                putExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON)
            }

            receiverSlot.captured.onReceive(context, intentOn)
            assertEquals(true, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}