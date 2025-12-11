package com.plantCare.plantcare.service

import android.util.Log
import com.google.gson.JsonObject
import com.plantCare.plantcare.database.PlantDao
import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.database.PlantDetailsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PlantSearchResult(
    val id: Long,
    val commonName: String,
    val scientificName: String,
)


class PlantDetailsRepository(
    private val plantService: PlantService,
    private val plantDetailsDao: PlantDetailsDao,
    private val plantDao: PlantDao,
) {
    private val cacheDuration = 31
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // todo handle errors
    suspend fun findPlant(plantName: String): List<PlantSearchResult>?{
        val response: JsonObject
        try {
             response = plantService.findPlant(plantName)
        } catch (_: Exception) {
            return null
        }

        val plants = response.getAsJsonArray("data")
        val result = mutableListOf<PlantSearchResult>()

        for (i in 0..<plants.size()) {
            val p: JsonObject = plants.get(i).asJsonObject
            result.add(PlantSearchResult(p["id"].asLong, p["common_name"].asString, p["scientific_name"].asString))
        }

        return result
    }

    suspend fun getPlantDetails(plantId: Long): PlantDetails? {
        val savedDetails = plantDetailsDao.getDetails(plantId)
        if (savedDetails != null) {
            val daysPassed = ChronoUnit.DAYS.between(savedDetails.updatedOn, LocalDate.now())
            if (daysPassed > 31) {
                repositoryScope.launch {
                    updatePlantDetails(plantId)
                }
            }

            return savedDetails
        }

        return updatePlantDetails(plantId)
    }

    suspend fun updatePlantDetails(plantId: Long): PlantDetails? {
        val plantApiId = plantDao.getApiId(plantId)

        if (plantApiId == null) {
            return null
        }

        val r: JsonObject
        try {
            r = plantService.getPlantDetails(plantApiId)
        } catch (_: Exception) {
            return null
        }

        val wateringValue = r["watering_general_benchmark"].asJsonObject["value"].asString
        val wateringUnit = r["watering_general_benchmark"].asJsonObject["unit"].asString
        val pruningMonths = r["pruning_month"].asJsonArray.map { it.asString }
        val soil = r["soil"].asJsonArray.map { it.asString }
        val sunlight = r["sunlight"].asJsonArray.map { it.asString }

        val details = PlantDetails(
            id = plantId,
            updatedOn = LocalDate.now(),
            commonName = r["common_name"].asString,
            scientificName = r["scientific_name"].asString,
            wateringValue = wateringValue,
            wateringUnit = wateringUnit,
            sunlight = sunlight,
            pruningMonths = pruningMonths,
            soil = soil,
            description = r["description"].asString
        )

        plantDetailsDao.updateDetails(details)
        return details
    }
}