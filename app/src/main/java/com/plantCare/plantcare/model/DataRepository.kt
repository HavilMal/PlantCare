package com.plantCare.plantcare.model

import android.content.Context
import java.util.Date
import com.plantCare.plantcare.utils.RandomUtil
import com.plantCare.plantcare.utils.FileUtil


class PlantRepository(private val appContext: Context, public val plantDao: PlantDao) {
    suspend fun insertPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }
    suspend fun insertPlant(name: String, species: String, plantedOn: Date = Date(),  wateringSchedule: WateringSchedule = WateringSchedule.MONTHLY) {
        val plantUUID = RandomUtil.genUUIDString()
        plantDao.insertPlant(Plant(name = name, species = species, plantedOn = plantedOn, wateringSchedule = wateringSchedule, dirPath = plantUUID))
        FileUtil.makeDir(appContext,"plants/$plantUUID",true)
    }
    suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant)
    }

    fun getPlantsDirPath(plantId: Long) : String{
        return plantDao.getPlantDirPath(plantId)
    }
}