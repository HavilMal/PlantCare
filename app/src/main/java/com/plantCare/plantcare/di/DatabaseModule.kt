package com.plantCare.plantcare.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.plantCare.plantcare.database.AppDatabase
import com.plantCare.plantcare.database.NotesDAO
import com.plantCare.plantcare.database.PlantDao
import com.plantCare.plantcare.database.PlantDetailsDao
import com.plantCare.plantcare.database.UserActivityDao
import com.plantCare.plantcare.database.WateringDao
import com.plantCare.plantcare.database.WeatherRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

const val DATABASE_NAME = "plant_care_db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = AppDatabase::class.java,
            name = DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePlantDao(database: AppDatabase): PlantDao {
        return database.plantDao()
    }
    @Provides
    @Singleton
    fun providePlantDetailsDAO(database: AppDatabase): PlantDetailsDao {
        return database.plantDetailsDAO()
    }

    @Provides
    @Singleton
    fun provideNotesDAO(database: AppDatabase): NotesDAO {
         return database.notesDAO()
    }
    @Provides
    @Singleton
    fun provideWeatherRecordDao(database: AppDatabase): WeatherRecordDao {
        return database.weatherRecordDao()
    }
    @Provides
    @Singleton
    fun provideWateringDao(database: AppDatabase): WateringDao {
        return database.wateringDao()
    }

    @Provides
    @Singleton
    fun provideUserActivityDao(database: AppDatabase): UserActivityDao {
        return database.userActivityDao()
    }

}