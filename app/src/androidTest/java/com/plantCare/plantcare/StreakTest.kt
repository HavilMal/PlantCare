package com.plantCare.plantcare

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.plantCare.plantcare.database.AppDatabase
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.SettingsRepository
import com.plantCare.plantcare.database.UserActivityRepository
import com.plantCare.plantcare.database.WateringInterval
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.service.WeatherService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class StreakTest {

    private lateinit var db: AppDatabase
    private lateinit var plantRepository: PlantRepository
    private lateinit var wateringRepository: WateringRepository
    private lateinit var userActivityRepository: UserActivityRepository
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val plantDao = db.plantDao()
        val wateringDao = db.wateringDao()
        val userActivityDao = db.userActivityDao()
        val weatherDao = db.weatherRecordDao()

        settingsRepository = SettingsRepository(context)
        weatherRepository = WeatherRepository(
            weatherService = object : WeatherService {
                override suspend fun getWeather5Days(lat: Double, lon: Double): JsonObject {
                    val response = JsonObject()
                    val listArray = JsonArray()
                    response.add("list", listArray)
                    return response
                }
            },
            weatherDao = weatherDao,
            settingsRepository = settingsRepository
        )

        plantRepository = PlantRepository(context, plantDao)
        wateringRepository = WateringRepository(wateringDao)
        userActivityRepository = UserActivityRepository(
            userActivityDao,
            weatherRepository,
            wateringRepository,
            plantRepository
        )
    }

    @After
    fun tearDown() {
        db.close()
    }


    @Test
    fun testStreak0() = runTest {
        val id = plantRepository.insertPlant(
            name = "name",
            description = "description",
            species = "species",
            plantedOn = LocalDate.of(2000, 1, 1),
            isIndoor = true,
            wateringInterval = WateringInterval.WEEK,
            apiId = null,
            sensorAddress = null,
        )
        plantRepository.setSchedule(id, setOf(DayOfWeek.MONDAY), WateringInterval.WEEK)


        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(1999, 12, 30)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(1999, 12, 31)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 1)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 2)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 3)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 4)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 5)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 6)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 7)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 8)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 9)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 10)).first())
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 11)).first())
    }

    @Test
    fun testStreak1() = runBlocking {
        // user logs on 01.01 and creates a plant
        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 1))
        val id = plantRepository.insertPlant(
            name = "name",
            description = "description",
            species = "species",
            plantedOn = LocalDate.of(2000, 1, 1),
            isIndoor = true,
            wateringInterval = WateringInterval.WEEK,
            apiId = null,
            sensorAddress = null,
        )
        plantRepository.setSchedule(id, setOf(DayOfWeek.MONDAY), WateringInterval.WEEK)

        // user wateres the plant on 03.01
        wateringRepository.insertWateringEntry(id,LocalDate.of(2000, 1, 3))
        // system updates streak data and user gets 1 streak
        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 3))
        assertEquals(1,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 3)).first())
        // user logs on 04.01 and the streak is still 1
        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 4))
        assertEquals(1,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 4)).first())
    }

    @Test
    fun testStreak2() = runBlocking {
        // user logs on 01.01 and creates a plant
        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 1))
        val id = plantRepository.insertPlant(
            name = "name",
            description = "description",
            species = "species",
            plantedOn = LocalDate.of(2000, 1, 1),
            isIndoor = true,
            wateringInterval = WateringInterval.WEEK,
            apiId = null,
            sensorAddress = null,
        )
        plantRepository.setSchedule(id, setOf(DayOfWeek.MONDAY), WateringInterval.WEEK)

        wateringRepository.insertWateringEntry(id,LocalDate.of(2000, 1, 3))

        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 9))
        assertEquals(1,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 9)).first())

        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 10))
        assertEquals(1,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 10)).first())

        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 11))
        assertEquals(0,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 11)).first())

        //Log.d("devo testing", "records = ${userActivityRepository.getAllRecords()}")
    }

    @Test
    fun testStreak3() = runBlocking {
        // user logs on 01.01 and creates a plant
        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 1))
        val id = plantRepository.insertPlant(
            name = "name",
            description = "description",
            species = "species",
            plantedOn = LocalDate.of(2000, 1, 1),
            isIndoor = true,
            wateringInterval = WateringInterval.WEEK,
            apiId = null,
            sensorAddress = null,
        )
        plantRepository.setSchedule(id, setOf(DayOfWeek.MONDAY), WateringInterval.WEEK)

        wateringRepository.insertWateringEntry(id,LocalDate.of(2000, 1, 3))

        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 4))

        plantRepository.deletePlant(plantRepository.getPlant(id).first()!!)

        userActivityRepository.updateUserStreakData(LocalDate.of(2000, 1, 4))
        assertEquals(1,userActivityRepository.getUserCurrentStreak(LocalDate.of(2000, 1, 4)).first())
    }
}