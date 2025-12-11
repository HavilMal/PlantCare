package com.plantCare.plantcare.di

import android.content.Context
import com.plantCare.plantcare.database.AppRepository
import com.plantCare.plantcare.database.NotesDAO
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.PlantDao
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.SettingsRepository
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

    @Provides
    @Singleton
    fun provideNotesRepository(
        notesDAO: NotesDAO
    ): NotesRepository {
        return NotesRepository(
            notesDAO = notesDAO
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        plantRepository: PlantRepository,
        settingsRepository: SettingsRepository
    ): AppRepository {
        return AppRepository(plantRepository,settingsRepository)
    }
}