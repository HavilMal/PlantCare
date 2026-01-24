package com.plantCare.plantcare.di

import android.content.Context
import com.plantCare.plantcare.service.SensorService
import com.plantCare.plantcare.service.SensorServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindSensorService(
        sensorService: SensorServiceImpl
    ) : SensorService

    companion object {
        @Provides
        @Singleton
        fun provideSensorService(@ApplicationContext context: Context): SensorServiceImpl =
            SensorServiceImpl(context)
    }
}