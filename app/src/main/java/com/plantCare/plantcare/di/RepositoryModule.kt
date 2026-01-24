package com.plantCare.plantcare.di

import android.content.Context
import com.plantCare.plantcare.database.AppRepository
import com.plantCare.plantcare.database.NotesDAO
import com.plantCare.plantcare.database.NotesRepositoryImpl
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.PlantDao
import com.plantCare.plantcare.database.PlantDetailsDao
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.PlantRepositoryImpl
import com.plantCare.plantcare.service.PlantDetailsRepositoryImpl
import com.plantCare.plantcare.service.PlantService
import com.plantCare.plantcare.database.SettingsRepository
import com.plantCare.plantcare.database.UserActivityDao
import com.plantCare.plantcare.database.UserActivityRepository
import com.plantCare.plantcare.database.WateringDao
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRecordDao
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.service.PlantDetailsRepository
import com.plantCare.plantcare.service.WeatherService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepository: NotesRepositoryImpl
    ): NotesRepository

    @Binds
    @Singleton
    abstract fun bindPlantRepository(
        plantRepository: PlantRepositoryImpl
    ): PlantRepository

    @Binds
    @Singleton
    abstract fun bindPlantDetailsRepository(
        plantRepository: PlantDetailsRepositoryImpl
    ): PlantDetailsRepository


    companion object {

        @Provides
        @Singleton
        fun providePlantRepository(
            @ApplicationContext context: Context,
            plantDao: PlantDao
        ): PlantRepositoryImpl {
            return PlantRepositoryImpl(
                appContext = context,
                plantDao = plantDao
            )
        }

        @Provides
        @Singleton
        fun provideNotesRepository(
            notesDAO: NotesDAO
        ): NotesRepositoryImpl {
            return NotesRepositoryImpl(
                notesDAO = notesDAO
            )
        }


        @Provides
        @Singleton
        fun providePlantDetailsRepository(
            plantService: PlantService,
            plantDetailsDao: PlantDetailsDao,
            plantDao: PlantDao,
        ): PlantDetailsRepositoryImpl {
            return PlantDetailsRepositoryImpl(
                plantService = plantService,
                plantDetailsDao = plantDetailsDao,
                plantDao = plantDao,
            )
        }

        @Provides
        @Singleton
        fun provideWeatherRepository(
            weatherService: WeatherService,
            weatherDao: WeatherRecordDao,
            settingsRepository: SettingsRepository
        ): WeatherRepository {
            return WeatherRepository(
                weatherService = weatherService,
                weatherDao = weatherDao,
                settingsRepository = settingsRepository
            )
        }

        @Provides
        @Singleton
        fun provideWateringRepository(
            wateringDao: WateringDao
        ): WateringRepository {
            return WateringRepository(
                wateringDao
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
        fun provideUserActivityRepository(
            userActivityDao: UserActivityDao,
            weatherRepository: WeatherRepository,
            wateringRepository: WateringRepository,
            plantRepository: PlantRepositoryImpl
        ): UserActivityRepository {
            return UserActivityRepository(
                userActivityDao,
                weatherRepository,
                wateringRepository,
                plantRepository
            )
        }


        @Provides
        @Singleton
        fun provideAppRepository(
            plantRepository: PlantRepositoryImpl,
            userActivityRepository: UserActivityRepository,
            weatherRepository: WeatherRepository,
            settingsRepository: SettingsRepository
        ): AppRepository {
            return AppRepository(
                plantRepository,
                userActivityRepository,
                weatherRepository,
                settingsRepository
            )
        }
    }
}