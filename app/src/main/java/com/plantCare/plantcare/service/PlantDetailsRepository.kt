package com.plantCare.plantcare.service

import android.util.Log
import com.google.gson.JsonObject
import com.plantCare.plantcare.database.PlantDao
import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.database.PlantDetailsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val apiIdLimit = 3000
    private val cacheDuration = 31
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // todo handle errors
    suspend fun findPlant(plantName: String): List<PlantSearchResult>? {
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
            if (p["id"].asLong <= apiIdLimit) {
                result.add(
                    PlantSearchResult(
                        p["id"].asLong,
                        p["common_name"].asString,
                        p["scientific_name"].asString
                    )
                )
            }
        }

        return result
    }

    fun getPlantDetails(plantId: Long): Flow<PlantDetails?> =
        flow {
            val savedDetails = plantDetailsDao.getDetails(plantId).first()
            if (savedDetails != null) {
                val daysPassed = ChronoUnit.DAYS.between(savedDetails.updatedOn, LocalDate.now())
                if (daysPassed > 31) {
                    updatePlantDetails(plantId).collect { details ->
                        emit(details)
                    }
                }

                emit(savedDetails)
                return@flow
            }

            updatePlantDetails(plantId).collect { details ->
                emit(details)
            }
            return@flow
        }

    fun updatePlantDetails(plantId: Long): Flow<PlantDetails?> =
        flow {
            val plantApiId = plantDao.getApiId(plantId)

            if (plantApiId == null) {
                emit(null)
                return@flow
            }

            val r: JsonObject
            try {
                r = plantService.getPlantDetails(plantApiId)
            } catch (e: Exception) {
                emit(null)
                return@flow
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
            emit(details)
            return@flow
        }.flowOn(Dispatchers.IO)
}