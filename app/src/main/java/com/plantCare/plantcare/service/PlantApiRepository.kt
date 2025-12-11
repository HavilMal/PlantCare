package com.plantCare.plantcare.service

import com.google.gson.JsonObject

class PlantApiRepository(private val plantService: PlantService) {

    suspend fun findPlant(plantName: String): List<JsonObject> {
        val response = plantService.findPlant(plantName)

        response["data"]
    }

    suspend fun getPlantTips(plantApiId: Long) {
        val response = plantService.getPlantDetails(plantApiId)

    }
}