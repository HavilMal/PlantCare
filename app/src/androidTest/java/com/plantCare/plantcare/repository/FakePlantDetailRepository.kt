package com.plantCare.plantcare.repository

import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.service.PlantDetailsRepository
import com.plantCare.plantcare.service.PlantSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePlantDetailRepository : PlantDetailsRepository {
    override suspend fun findPlant(plantName: String): List<PlantSearchResult>? {
        return listOf()
    }

    override fun getPlantDetails(plantId: Long): Flow<PlantDetails?> = flow {
        emit(null)
    }

    override fun updatePlantDetails(plantId: Long): Flow<PlantDetails?> = flow {
        emit(null)
    }
}