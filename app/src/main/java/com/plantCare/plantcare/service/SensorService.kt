package com.plantCare.plantcare.service

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface SensorService {
    fun scanForSensor(): Flow<BluetoothDevice?>

    fun getSensorDataFlow(address: String): Flow<SensorData?>

    fun getBluetoothStateFlow(): Flow<Boolean>
}