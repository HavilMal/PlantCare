package com.plantCare.plantcare.di

import com.plantCare.plantcare.BuildConfig
import com.plantCare.plantcare.service.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WeatherServiceModule :  ApiServiceModule<WeatherService>() {
    override val apiKeyParamName: String = "appid"
    override val apiKey: String = BuildConfig.WEATHER_API_KEY
    override val baseUrl: String = "https://api.openweathermap.org/"
    override val serviceClass: Class<WeatherService> = WeatherService::class.java
    @Provides
    @Singleton
    fun weatherService(): WeatherService {
        return provideApiService()
    }
}