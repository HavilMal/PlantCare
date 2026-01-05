package com.plantCare.plantcare.database

import android.util.Log
import com.plantCare.plantcare.service.WeatherService
import java.sql.Timestamp
import java.time.LocalDate

class WeatherRepository (
    private val weatherService: WeatherService,
    val weatherDao: WeatherRecordDao,
    private val settingsRepository: SettingsRepository,
){
    companion object{
        val WEATHER_IDS_WITH_RAIN: Array<Int> = arrayOf(
            500, 501, 502, 503, 504, 511, 520, 521, 522, 532,
            200, 201, 202
        )
    }
    fun hasRainedOn(date: LocalDate) : Boolean {
        return weatherDao.hasRainedOn(date)
    }

    suspend fun fetchWeatherData(){
        val (lat, lon) = settingsRepository.getLocation()

        if((lat == null && lon == null)) return

        val response = weatherService.getWeather5Days(lat ?: 0.0,lon ?: 0.0)
        val r = response.getAsJsonObject().getAsJsonArray("list")
//        Log.d("devo","response = $response")
        r.forEachIndexed { index, item ->
            val obj = item.asJsonObject
            val dt = obj.get("dt").asLong

            val weatherArray = obj.getAsJsonArray("weather")
            val weatherMain = weatherArray[0].asJsonObject.get("main").asString
            val weatherId : Int = weatherArray[0].asJsonObject.get("id").asInt
            val weatherDescription = weatherArray[0].asJsonObject.get("description")

            weatherDao.insertRecord(
                WeatherRecord(
                    java.sql.Timestamp(dt * 1000),
                    WEATHER_IDS_WITH_RAIN.contains(weatherId),
                    null
                )
            )
        }
    }

    suspend fun updateCityName(){
        val (lat, lon) = settingsRepository.getLocation()
        if((lat == null && lon == null)) return
        val response = weatherService.getWeather5Days(lat ?: 0.0,lon ?: 0.0)
        settingsRepository.setLocationName(response.getAsJsonObject("city").get("name").asString)
    }

}