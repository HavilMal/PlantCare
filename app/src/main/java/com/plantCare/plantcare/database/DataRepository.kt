package com.plantCare.plantcare.database

import android.content.Context
import android.util.Log
import com.plantCare.plantcare.R
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.utils.RandomUtil
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

const val PLANTS_DIR: String = "plants/"

class PlantRepository(
    val appContext: Context,
    val plantDao: PlantDao,
) {
    companion object {
        const val PLANT_PHOTO_PREFIX = "image-"
        fun genPlantPhotoId(): String {
            return System.currentTimeMillis().toString()
        }
    }

    suspend fun insertPlant(
        name: String,
        description: String,
        species: String,
        plantedOn: LocalDate = LocalDate.now(),
        isIndoor: Boolean = true,
        wateringInterval: WateringInterval = WateringInterval.WEEK,
        apiId: Long? = null,
    ): Long {
        val plantUUID = RandomUtil.genUUIDString()
        val id = plantDao.insertPlant(
            Plant(
                name = name,
                description = description,
                species = species,
                plantedOn = plantedOn,
                dirPath = plantUUID,
                isIndoor = isIndoor,
                createdOn = LocalDate.now(),
                wateringInterval = wateringInterval,
                apiId = apiId,
            )
        )
        FileUtil.makeDir(appContext, "$PLANTS_DIR$plantUUID", true)

        Log.d("plantRepository", "inserted plant")
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
        return plantDao.getPlantDirPath(plantId)
    }

    suspend fun addPlantPhoto(plantId: Long, photo: File) {
        val plantDir = getPlantsDirPath(plantId)
        val destFile = File(
            appContext.filesDir,
            "$PLANTS_DIR$plantDir/${PLANT_PHOTO_PREFIX}${genPlantPhotoId()}.jpg"
        )
        photo.copyTo(destFile, overwrite = true)
    }

    suspend fun getPlantPhotos(plantId: Long): List<File> {
        val plantDir = getPlantsDirPath(plantId)
        return FileUtil.getFiles(appContext, "$PLANTS_DIR$plantDir").filter { file ->
            file.name.startsWith(PLANT_PHOTO_PREFIX)
        }
    }

    suspend fun updatePlant(
        id: Long,
        name: String? = null,
        isIndoor: Boolean? = null,
        species: String? = null,
        plantedOn: LocalDate? = null,
        wateringInterval: WateringInterval? = null,
        apiId: Long? = null,
    ) {
        plantDao.getPlantFlow(id).collect { plant ->
            plantDao.updatePlant(
                plant.copy(
                    id = id,
                    name = name ?: plant.name,
                    isIndoor = isIndoor ?: plant.isIndoor,
                    species = species ?: plant.species,
                    plantedOn = plantedOn ?: plant.plantedOn,
                    wateringInterval = wateringInterval ?: plant.wateringInterval,
                    apiId = apiId ?: plant.apiId,
                )
            )
        }
    }
}

class AppRepository(
    val plantRepository: PlantRepository
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
        plantRepository.addPlantPhoto(id, cactusImageFile)
        plantRepository.addPlantPhoto(id, cactusImageFile)
        plantRepository.addPlantPhoto(id, cactusImageFile)

        plantRepository.insertPlant("Storczyk Tadek", "Fajny jest", "Storczyk")
    }


}
