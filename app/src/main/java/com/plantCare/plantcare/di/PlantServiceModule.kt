package com.plantCare.plantcare.di

import com.plantCare.plantcare.BuildConfig
import com.plantCare.plantcare.service.PlantService
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
object PlantServiceModule : ApiServiceModule<PlantService>() {
    override val apiKeyParamName: String = "key"
    override val apiKey: String = BuildConfig.PLANT_API_KEY
    override val baseUrl: String = "https://perenual.com/api/v2/"
    override val serviceClass: Class<PlantService> = PlantService::class.java

    @Provides
    @Singleton
    fun plantService(): PlantService {
        return provideApiService()
    }
}