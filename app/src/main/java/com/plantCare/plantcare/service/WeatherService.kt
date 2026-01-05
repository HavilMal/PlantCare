package com.plantCare.plantcare.service

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    //api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}
    // api.openweathermap.org/data/2.5/forecast?lat=0.0&lon=0.0&appid=a59446abe653a07ceadf0e5faa62d792
    @GET("data/2.5/forecast")
    suspend fun getWeather5Days(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): JsonObject
}