package com.plantCare.plantcare.database

import com.plantCare.plantcare.utils.RandomUtil
import java.util.Date


object AppLogic {

    fun plantNeedsWatering(plant: Plant, date: Date): Boolean{
        return RandomUtil.genBool()
    }
}