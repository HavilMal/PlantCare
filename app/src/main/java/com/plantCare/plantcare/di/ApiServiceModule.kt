package com.plantCare.plantcare.di

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

class ApiKeyQueryInterceptor(private val apiKey: String, private val paramName: String ="key") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter(paramName, apiKey)
            .build()

        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}


abstract class ApiServiceModule<T : Any> {
    protected abstract val apiKeyParamName: String
    protected abstract val apiKey: String
    protected abstract val baseUrl: String
    protected abstract val serviceClass: Class<T>

    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyQueryInterceptor(apiKey,apiKeyParamName))
            .build()

    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun provideApiService(): T {
        return provideRetrofit(provideOkHttpClient()).create(serviceClass)
    }
}