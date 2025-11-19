package com.plantCare.plantcare.database

import android.content.Context
import java.util.Date
import com.plantCare.plantcare.utils.RandomUtil
import com.plantCare.plantcare.utils.FileUtil
import java.io.File
import java.time.LocalDate


const val PLANTS_DIR: String = "plants/"

class PlantRepository(
    private val appContext: Context,
    public val plantDao: PlantDao,
) {

    suspend fun insertPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }
    suspend fun insertPlant(name: String, description: String, species: String, plantedOn: LocalDate = LocalDate.now(), wateringSchedule: WateringSchedule = WateringSchedule.MONTHLY) {
        val plantUUID = RandomUtil.genUUIDString()
        plantDao.insertPlant(Plant(name = name, description = description,species = species, plantedOn = plantedOn, wateringSchedule = wateringSchedule, dirPath = plantUUID))
        FileUtil.makeDir(appContext,"$PLANTS_DIR$plantUUID",true)
    }
    suspend fun deletePlant(plant: Plant) {
        FileUtil.deleteDir(appContext,plantDao.getPlantDirPath(plant.id))
        plantDao.deletePlant(plant)
    }
    suspend fun deleteAllPlants() {
        FileUtil.deleteDir(appContext,PLANTS_DIR)
        plantDao.deleteAllPlants()
    }
    fun getPlantsDirPath(plantId: Long) : String{
        return plantDao.getPlantDirPath(plantId)
    }

    fun addPlantPhoto(plantId: Long,photo: File) {
        val plantDir = getPlantsDirPath(plantId)
        val destFile = File(plantDir, "photo-${System.currentTimeMillis()}.jpg")
        photo.copyTo(destFile, overwrite = true)
    }
}

class AppRepository(
    val plantRepository: PlantRepository
) {
    suspend fun seedDatabase() {
        plantRepository.deleteAllPlants()
        plantRepository.insertPlant("Kaktus Maksiu","Nazwa po moim zmarłym dziadku","Kaktus Kaktus")
        plantRepository.insertPlant("Storczyk Tadek","Fajny jest","Storczyk")
    }


}
