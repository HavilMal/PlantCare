package com.plantCare.plantcare.database

import android.content.Context
import com.plantCare.plantcare.R
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.utils.RandomUtil
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.abs

const val PLANTS_DIR: String = "plants/"

class PlantRepository(
    val appContext: Context,
    val plantDao: PlantDao,
) {
    companion object {
        const val PLANT_IMAGE_DIR_NAME = "images"
        fun genPlantPhotoId(): String {
            return System.currentTimeMillis().toString()
        }
    }

    suspend fun insertPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }

    suspend fun insertPlant(
        name: String,
        description: String,
        species: String,
        plantedOn: LocalDate = LocalDate.now(),
        isIndoor: Boolean = true,
        wateringInterval: WateringInterval = WateringInterval.WEEK,
    ): Long {
        val plantUUID = RandomUtil.genUUIDString()
        FileUtil.makeDir(appContext, "$PLANTS_DIR$plantUUID", true)
        val id = plantDao.insertPlant(
            Plant(
                name = name,
                description = description,
                species = species,
                plantedOn = plantedOn,
                dirPath = plantUUID,
                isIndoor = isIndoor,
                createdOn = LocalDate.now(),
                wateringInterval = wateringInterval
            )
        )
        FileUtil.makeDir(appContext, "$PLANTS_DIR$plantUUID/$PLANT_IMAGE_DIR_NAME", true)
        return id
    }

    suspend fun deletePlant(plant: Plant) {
        FileUtil.deleteDir(appContext, plantDao.getPlantDirPath(plant.id))
        plantDao.deletePlant(plant)
    }

    suspend fun deleteAllPlants() {
        FileUtil.deleteDir(appContext, PLANTS_DIR)
        plantDao.deleteAllPlants()
    }

    fun getPlant(plantId: Long): Flow<Plant?> {
        return plantDao.getPlantFlow(plantId)
    }

    fun getSchedule(plantId: Long): Flow<List<WateringSchedule>> {
        return plantDao.getWateringSchedule(plantId)
    }

    fun getSchedules(): Flow<List<WateringSchedule>> {
        return plantDao.getWateringSchedules()
    }

    fun getPlantWateringSchedules(): Flow<List<PlantWateringSchedule>> {
        return plantDao.getPlantWateringSchedules()
    }

    suspend fun setSchedule(plantId: Long, days: Set<DayOfWeek>, interval: WateringInterval) {
        plantDao.setSchedule(plantId, days, interval)
    }

    suspend fun getPlantsDirPath(plantId: Long): String? {
        return "$PLANTS_DIR${plantDao.getPlantDirPath(plantId)}"
    }

    fun getPlantsDirPath(plant: Plant): String {
        return "$PLANTS_DIR${plant.dirPath}"
    }

    fun getPlantsImageDirPath(plantDirPath: String): String {
        return "$plantDirPath/$PLANT_IMAGE_DIR_NAME"
    }

    suspend fun addPlantPhoto(plantId: Long, photo: File) {
        val plantDir = getPlantsDirPath(plantId)
        val destFile = File(
            appContext.filesDir,
            "$plantDir/${PLANT_IMAGE_DIR_NAME}/${genPlantPhotoId()}.jpg"
        )
        photo.copyTo(destFile, overwrite = true)
    }

    fun deletePlantImage(file: File) {
        FileUtil.delete(file)
    }

    suspend fun getPlantPhotos(plantId: Long): List<File> {
        return FileUtil.getFiles(appContext, "${getPlantsDirPath(plantId)}/$PLANT_IMAGE_DIR_NAME")
    }

    suspend fun updatePlant(
        id: Long,
        name: String? = null,
        isIndoor: Boolean? = null,
        species: String? = null,
        plantedOn: LocalDate? = null,
        wateringInterval: WateringInterval? = null,
    ) {
        plantDao.getPlantFlow(id).collect { plant ->
            plantDao.updatePlant(
                plant.copy(
                    id = id,
                    name = name ?: plant.name,
                    isIndoor = isIndoor ?: plant.isIndoor,
                    species = species ?: plant.species,
                    plantedOn = plantedOn ?: plant.plantedOn,
                    wateringInterval = wateringInterval ?: plant.wateringInterval
                )
            )
        }
    }
}

class AppRepository(
    val plantRepository: PlantRepository,
    val settingsRepository: SettingsRepository
) {
    suspend fun seedDatabase() {
        plantRepository.deleteAllPlants()
        var id = plantRepository.insertPlant(
            "Kaktus Maksiu",
            "Nazwa po moim zmarłym dziadku",
            "Kaktus Kaktus"
        )
        val cactusImageFile = FileUtil.drawableAsFile(
            plantRepository.appContext,
            R.drawable.cactus,
            "${System.currentTimeMillis()}.png"
        )
        plantRepository.addPlantPhoto(id, cactusImageFile)
        plantRepository.addPlantPhoto(id, cactusImageFile)

        plantRepository.insertPlant("Storczyk Tadek", "Fajny jest", "Storczyk")
    }
    suspend fun setDefaultSettings(){
        settingsRepository.setDefault()
    }
}