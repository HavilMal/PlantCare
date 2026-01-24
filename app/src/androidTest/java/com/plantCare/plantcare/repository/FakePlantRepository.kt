package com.plantCare.plantcare.repository

import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringInterval
import com.plantCare.plantcare.database.WateringSchedule
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

class FakePlantRepository : PlantRepository {
    var plants = mutableListOf<Plant>()
    val wateringSchedules = mutableListOf<WateringSchedule>()

    override fun getPlantDirName(plantId: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlant(
        name: String,
        description: String,
        species: String,
        plantedOn: LocalDate,
        isIndoor: Boolean,
        wateringInterval: WateringInterval,
        apiId: Long?,
        sensorAddress: String?
    ): Long {
        val id = plants.size.toLong() + 1L
        plants.add(
            Plant(
                id = plants.size.toLong() + 1L,
                name = name,
                description = description,
                isIndoor = isIndoor,
                species = species,
                plantedOn = plantedOn,
                createdOn = LocalDate.now(),
                wateringInterval = wateringInterval,
                apiId = apiId,
                sensorAddress = sensorAddress
            )
        )

        return id
    }

    override suspend fun deletePlant(plant: Plant) {
        plants.removeAll { it.id == plant.id }
    }

    override suspend fun deleteAllPlants() {
        plants.clear()
    }

    override fun getPlant(plantId: Long): Flow<Plant?> = flow {
        emit(plants.first { it.id == plantId })
    }

    override fun getAllPlantsFlow(): Flow<List<Plant>> = flow {
        emit(plants)
    }

    override suspend fun getAllPlants(): List<Plant> {
        return plants
    }

    override suspend fun getAllPlantIds(): List<Long> {
        return plants.map { it.id }
    }

    override fun getSchedule(plantId: Long): Flow<List<WateringSchedule>> = flow {
        emit(wateringSchedules.filter {it.plant == plantId})
    }

    override fun getSchedules(): Flow<List<WateringSchedule>> = flow{
        emit(wateringSchedules)
    }

    override fun getPlantWateringSchedules(): Flow<List<PlantWateringSchedule>> {
        TODO("Not yet implemented")
    }

    override fun getPlantsTumbnailFlow(): Flow<Map<Long, File?>> {
        TODO("Not yet implemented")
    }

    override suspend fun setSchedule(
        plantId: Long,
        days: Set<DayOfWeek>,
        interval: WateringInterval
    ) {
        TODO("Not yet implemented")
    }

    override fun getPlantsDirPath(plantId: Long): String {
        TODO("Not yet implemented")
    }

    override fun getPlantsDirPath(plant: Plant): String {
        TODO("Not yet implemented")
    }

    override fun getPlantsMediaDirPath(plantDirPath: String?): String {
        TODO("Not yet implemented")
    }

    override fun getPlantsMediaDirPath(plantId: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun addPlantMedia(plantId: Long, media: File) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlantMedia(file: File) {
        TODO("Not yet implemented")
    }

    override fun getPlantMediaFlow(plantId: Long): Flow<List<File>> {
        TODO("Not yet implemented")
    }

    override suspend fun setApiId(plantId: Long, id: Long?) {
        TODO("Not yet implemented")
    }

    override suspend fun setSensorAddress(id: Long, address: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlant(
        id: Long,
        name: String?,
        description: String?,
        species: String?,
        isIndoor: Boolean?,
        plantedOn: LocalDate?,
        wateringInterval: WateringInterval?
    ) {
        TODO("Not yet implemented")
    }
}