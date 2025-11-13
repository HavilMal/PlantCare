package com.plantCare.plantcare.di

import android.content.Context
import com.plantCare.plantcare.model.PlantDao
import com.plantCare.plantcare.model.PlantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePlantRepository(
        @ApplicationContext context: Context,
        plantDao: PlantDao
    ): PlantRepository {
        return PlantRepository(
            appContext = context,
            plantDao = plantDao
        )
    }
}