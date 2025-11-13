package com.plantCare.plantcare.model

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AppViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {
    suspend fun seedDatabase() {
        appRepository.seedDatabase()
    }
}