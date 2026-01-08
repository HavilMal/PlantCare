package com.plantCare.plantcare.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.PlantWateringInfo
import com.plantCare.plantcare.database.UserActivityDao
import com.plantCare.plantcare.database.UserActivityRepository
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.logic.PlantWateringStatus
import com.plantCare.plantcare.logic.WateringLogicEvaluator
import com.plantCare.plantcare.logic.WateringStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class PlantWateringSection(
    val sectionName: String,
    val acceptedStatuses: List<WateringStatus>
) {
    NEEDS_WATERING("Water today", listOf(WateringStatus.NEEDS_WATERING)),
    WATERED_TODAY("Already watered today", listOf(WateringStatus.WATERED_BY_WEATHER, WateringStatus.WATERED_BY_USER, WateringStatus.WATERED_BY_USER_AND_WEATHER)),
    NEEDS_NO_WATERING("Needs no watering", listOf(WateringStatus.NEEDS_NO_WATERING));
}

data class PlantWateringCardInfo(
    val plant: Plant,
    val wateringStatus: WateringStatus
)
data class HomeUiState(
    val plantWateringStatuses: List<PlantWateringCardInfo> = emptyList(),
    val userCurrentStreak: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wateringRepository: WateringRepository,
    private val weatherRepository: WeatherRepository,
    private val plantRepository: PlantRepository,
    private val userActivityRepository: UserActivityRepository
) : ViewModel() {

    val wateringLogicEvaluator : WateringLogicEvaluator = WateringLogicEvaluator(weatherRepository,wateringRepository)
    private val homeFlow = MutableStateFlow(HomeUiState())
    val homeState = homeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            wateringLogicEvaluator.getPlantsWaterStatusToday()
                .collect { statuses ->
                    val cards = statuses.mapNotNull { pws ->
                        val plant = plantRepository.getPlant(pws.plantId).first()
                        plant?.let { PlantWateringCardInfo(it, pws.status) }
                    }
                    homeFlow.update {
                        it.copy(plantWateringStatuses = cards)
                    }
                }
        }

        viewModelScope.launch {
            userActivityRepository.getUserCurrentStreak().collect { streak ->
                homeFlow.update {
                    it.copy(userCurrentStreak = streak)
                }
            }

        }
//        viewModelScope.launch {
//            updateStreakData()
//        }
//        viewModelScope.launch {
//            homeFlow
//                .map { it.plantWateringStatuses }
//                .distinctUntilChanged()
//                .collect { statuses ->
//                    if (statuses.isNotEmpty() &&
//                        statuses.none { it.wateringStatus == WateringStatus.NEEDS_WATERING }) {
//                        Log.d("devo", "All plants watered today!!")
//                        userActivityRepository.insertUserStreakRecord()
//                    }
//                }
//        }
    }

    fun allPlantsWatered() : Boolean {
        Log.d("devo","VM = " + homeFlow.value.plantWateringStatuses.toString())
        return homeFlow.value.plantWateringStatuses.none { pws -> pws.wateringStatus == WateringStatus.NEEDS_WATERING }
    }
//    suspend fun checkTodayStreak(){
//        if(allPlantsWatered()) {
//            Log.d("devo","all plants watered today!!")
//            userActivityRepository.insertUserStreakRecord()
//        }
//    }
    suspend fun waterPlant(plantId: Long){
        wateringRepository.insertWateringEntry(plantId)
    }

    fun logWeatherData() {
        Log.d("devo","weather entries:")
        viewModelScope.launch(Dispatchers.IO) {
            val records = weatherRepository.weatherDao.getAllRecords()
            records.forEach { r ->
                Log.d("devo",r.toString())
            }
        }
    }

    suspend fun updateStreakData(){
        userActivityRepository.updateUserStreakData()
    }
}