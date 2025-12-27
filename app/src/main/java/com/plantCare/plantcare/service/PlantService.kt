package com.plantCare.plantcare.service

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PlantService {
    @GET("species/details/{id}")
    suspend fun getPlantDetails(@Path("id") plantApiId: Long): JsonObject

    @GET("species-list")
    suspend fun findPlant(@Query("q") plantName: String): JsonObject
}