package com.plantCare.plantcare.viewModel

import androidx.lifecycle.ViewModel
import com.plantCare.plantcare.database.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {
    suspend fun seedDatabase() {
        appRepository.seedDatabase()
    }
}