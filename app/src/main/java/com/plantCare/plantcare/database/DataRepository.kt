package com.plantCare.plantcare.database

import android.content.Context
import com.plantCare.plantcare.R
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import com.plantCare.plantcare.utils.FileUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

const val PLANTS_DIR: String = "plants/"

class PlantRepository(
    val appContext: Context,
    val plantDao: PlantDao,
) {
    companion object {
        const val PLANT_MEDIA_DIR_NAME = "media"
        fun genPlantMediaId(): String {
            return System.currentTimeMillis().toString()
        }
    }

    fun getPlantDirName(plantId: Long): String {
        return plantId.toString()
    }

    suspend fun insertPlant(
        name: String,
        description: String,
        species: String,
        plantedOn: LocalDate = LocalDate.now(),
        isIndoor: Boolean = true,
        wateringInterval: WateringInterval = WateringInterval.WEEK,
        apiId: Long? = null,
        sensorAddress: String? = null,
    ): Long {
        val id = plantDao.insertPlant(
            Plant(
                name = name,
                description = description,
                species = species,
                plantedOn = plantedOn,
                isIndoor = isIndoor,
                createdOn = LocalDate.now(),
                wateringInterval = wateringInterval,
                apiId = apiId,
                sensorAddress = sensorAddress
            )
        )
        FileUtil.makeDir(
            appContext,
            "$PLANTS_DIR${getPlantDirName(id)}/$PLANT_MEDIA_DIR_NAME",
            true
        )
        return id
    }

    suspend fun deletePlant(plant: Plant) {
        FileUtil.deleteDir(appContext, getPlantsDirPath(plant.id))
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

    fun getPlantsTumbnailFlow(): Flow<Map<Long, File?>> {
        return plantDao.getPlantThumbnails().map {
            it.mapValues { (id, path) ->
                val plantMediaDirPath: String = getPlantsMediaDirPath(id)
                path?.let { File(appContext.filesDir, "$plantMediaDirPath/$path") }
            }
        }
    }

    suspend fun setSchedule(plantId: Long, days: Set<DayOfWeek>, interval: WateringInterval) {
        plantDao.setSchedule(plantId, days, interval)
    }

    fun getPlantsDirPath(plantId: Long): String {
        return "$PLANTS_DIR${getPlantDirName(plantId)}"
    }

    fun getPlantsDirPath(plant: Plant): String {
        return getPlantsDirPath(plant.id)
    }

    fun getPlantsMediaDirPath(plantDirPath: String?): String {
        return "$plantDirPath/$PLANT_MEDIA_DIR_NAME"
    }

    fun getPlantsMediaDirPath(plantId: Long): String {
        return getPlantsMediaDirPath(getPlantsDirPath(plantId))
    }

    suspend fun addPlantMedia(plantId: Long, media: File) {
        val new_name = "${genPlantMediaId()}.${media.extension}"
        plantDao.insertPlantMedia(plantId, new_name)
        val destFile = File(
            appContext.filesDir,
            "${getPlantsMediaDirPath(plantId)}/$new_name"
        )
        media.copyTo(destFile, overwrite = true)
    }

    suspend fun deletePlantMedia(file: File) {
        plantDao.deletePlantMedia(file.name)
        FileUtil.delete(file)
    }

    fun getPlantMediaFlow(plantId: Long): Flow<List<File>> {
        val plantMediaDirPath: String = getPlantsMediaDirPath(plantId)
        return plantDao.getPlantMediaFlow(plantId)
            .map { mediaNames ->
                mediaNames.map { mediaName ->
                    File(appContext.filesDir, "$plantMediaDirPath/$mediaName")
                }
            }
    }

    suspend fun setApiId(plantId: Long, id: Long?) {
        plantDao.updateApiId(plantId, id)
    }

    suspend fun setSensorAddress(id: Long, address: String?) {
        plantDao.updateSensorAddress(id, address)
    }

    suspend fun updatePlant(
        id: Long,
        name: String? = null,
        description: String? = null,
        species: String? = null,
        isIndoor: Boolean? = null,
        plantedOn: LocalDate? = null,
        wateringInterval: WateringInterval? = null,
    ) {
        plantDao.getPlantFlow(id).first()?.let { plant ->
            plantDao.updatePlant(
                plant.copy(
                    id = id,
                    name = name ?: plant.name,
                    description = description ?: plant.description,
                    species = species ?: plant.species,
                    isIndoor = isIndoor ?: plant.isIndoor,
                    plantedOn = plantedOn ?: plant.plantedOn,
                    wateringInterval = wateringInterval ?: plant.wateringInterval,
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

        plantRepository.addPlantMedia(id, cactusImageFile)
        plantRepository.addPlantMedia(id, cactusImageFile)

        plantRepository.insertPlant("Storczyk Tadek", "Fajny jest", "Storczyk")
    }

    suspend fun setDefaultSettings() {
        settingsRepository.setDefault()
    }
}
