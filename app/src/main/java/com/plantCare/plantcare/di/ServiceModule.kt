package com.plantCare.plantcare.di

import com.plantCare.plantcare.BuildConfig
import com.plantCare.plantcare.service.PlantService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyQueryInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
   @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyQueryInterceptor(BuildConfig.PLANT_API_KEY))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://perenual.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): PlantService=
        retrofit.create(PlantService::class.java)
}