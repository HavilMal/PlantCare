package com.plantCare.plantcare.service

import android.bluetooth.BluetoothDevice
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeSensorService(
    public var timeoutMillis: Long = 0L,
    public var hasSensor: Boolean = true,
    public var humidity: Float = 0.80F,
    public var light: Float = 0.30F,
    public var hasBluetooth: Boolean = true,
    public var address: String = "00:11:22:33:44:55",
) : SensorService {
    private val mockDevice = mockk<BluetoothDevice>(relaxed = true)

    init {
        every { mockDevice.address } returns address
        every { mockDevice.getAddress() } returns address
    }

    override fun scanForSensor(): Flow<BluetoothDevice?> = flow {
        delay(timeoutMillis)
        emit(
            if (hasSensor) {
                mockDevice
            } else {
                null
            }
        )
    }

    override fun getSensorDataFlow(address: String): Flow<SensorData?> = flow {
        emit(
            SensorData(
                humidity = humidity,
                light = light,
            )
        )
    }

    override fun getBluetoothStateFlow(): Flow<Boolean> = flow {
        emit(hasBluetooth)
    }
}