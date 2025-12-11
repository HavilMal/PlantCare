package com.plantCare.plantcare.service

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class PlantSearchResult(
    val id: Long,
    val commonName: String,
    val scientificName: String,
)

class PlantApiRepository(private val plantService: PlantService) {

    // todo handle errors
    suspend fun findPlant(plantName: String): List<PlantSearchResult>{
        val response = plantService.findPlant(plantName)

        Log.d("plantApiRepository", response.toString())
        val plants = response.getAsJsonArray("data")
        val result = mutableListOf<PlantSearchResult>()

        for (i in 0..plants.size()) {
            val p: JsonObject = plants.get(i).asJsonObject
            result.add(PlantSearchResult(p["id"].asLong, p["common_name"].asString, p["scientific_name"].asString))
        }

        return result
    }

    suspend fun getPlantTips(plantApiId: Long) {
        val response = plantService.getPlantDetails(plantApiId)

    }
}