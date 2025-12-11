package com.plantCare.plantcare.service

import android.util.Log
import com.google.gson.JsonObject

class PlantApiRepository(private val plantService: PlantService) {

    suspend fun findPlant(plantName: String) {
        val response = plantService.findPlant(plantName)

        Log.d("plantApiRepository", response.toString())
    }

    suspend fun getPlantTips(plantApiId: Long) {
        val response = plantService.getPlantDetails(plantApiId)

    }
}