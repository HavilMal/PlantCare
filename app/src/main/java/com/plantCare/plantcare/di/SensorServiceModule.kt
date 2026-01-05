package com.plantCare.plantcare.di

import android.content.Context
import com.plantCare.plantcare.service.SensorService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideSensorService(@ApplicationContext context: Context): SensorService =
        SensorService(context)
}