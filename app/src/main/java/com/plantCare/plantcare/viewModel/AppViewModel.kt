package com.plantCare.plantcare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {
    suspend fun seedDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
                appRepository.seedDatabase()
            }
    }

    fun onAppStart() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.weatherRepository.fetchWeatherData()
        }
    }
}