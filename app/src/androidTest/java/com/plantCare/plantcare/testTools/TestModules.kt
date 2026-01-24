package com.plantCare.plantcare.testTools
//
//import android.content.Context
//import androidx.room.Room
//import com.google.gson.JsonArray
//import com.google.gson.JsonObject
//import com.plantCare.plantcare.database.AppDatabase
//import com.plantCare.plantcare.database.AppRepository
//import com.plantCare.plantcare.database.NotesDAO
//import com.plantCare.plantcare.database.NotesRepository
//import com.plantCare.plantcare.database.PlantDao
//import com.plantCare.plantcare.database.PlantDetailsDao
//import com.plantCare.plantcare.database.PlantRepository
//import com.plantCare.plantcare.database.SettingsRepository
//import com.plantCare.plantcare.database.UserActivityDao
//import com.plantCare.plantcare.database.UserActivityRepository
//import com.plantCare.plantcare.database.WateringDao
//import com.plantCare.plantcare.database.WateringRepository
//import com.plantCare.plantcare.database.WeatherRecordDao
//import com.plantCare.plantcare.database.WeatherRepository
//import com.plantCare.plantcare.di.DatabaseModule
//import com.plantCare.plantcare.di.RepositoryModule
//import com.plantCare.plantcare.di.WeatherServiceModule
//import com.plantCare.plantcare.service.PlantDetailsRepository
//import com.plantCare.plantcare.service.PlantService
//import com.plantCare.plantcare.service.WeatherService
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import dagger.hilt.testing.TestInstallIn
//import javax.inject.Singleton
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [DatabaseModule::class]
//)
//object TestDatabaseModule {
//
//    @Provides
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun providePlantDao(database: AppDatabase): PlantDao {
//        return database.plantDao()
//    }
//    @Provides
//    @Singleton
//    fun providePlantDetailsDAO(database: AppDatabase): PlantDetailsDao {
//        return database.plantDetailsDAO()
//    }
//
//    @Provides
//    @Singleton
//    fun provideNotesDAO(database: AppDatabase): NotesDAO {
//        return database.notesDAO()
//    }
//    @Provides
//    @Singleton
//    fun provideWeatherRecordDao(database: AppDatabase): WeatherRecordDao {
//        return database.weatherRecordDao()
//    }
//    @Provides
//    @Singleton
//    fun provideWateringDao(database: AppDatabase): WateringDao {
//        return database.wateringDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserActivityDao(database: AppDatabase): UserActivityDao {
//        return database.userActivityDao()
//    }
//
//}
//
//
//
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [RepositoryModule::class]
//)
//object TestRepositoryModule {
//
//    @Provides
//    @Singleton
//    fun providePlantRepository(
//        @ApplicationContext context: Context,
//        plantDao: PlantDao
//    ): PlantRepository {
//        return PlantRepository(
//            appContext = context,
//            plantDao = plantDao
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideNotesRepository(
//        notesDAO: NotesDAO
//    ): NotesRepository {
//        return NotesRepository(
//            notesDAO = notesDAO
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun providePlantDetailsRepository(
//        plantService: PlantService,
//        plantDetailsDao: PlantDetailsDao,
//        plantDao: PlantDao,
//    ): PlantDetailsRepository {
//        return PlantDetailsRepository(
//            plantService = plantService,
//            plantDetailsDao = plantDetailsDao,
//            plantDao = plantDao,
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideWeatherRepository(
//        weatherService: WeatherService,
//        weatherDao: WeatherRecordDao,
//        settingsRepository: SettingsRepository
//    ): WeatherRepository {
//        return WeatherRepository(
//            weatherService = weatherService,
//            weatherDao = weatherDao,
//            settingsRepository = settingsRepository
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideWateringRepository(
//        wateringDao: WateringDao
//    ): WateringRepository {
//        return WateringRepository(
//            wateringDao
//        )
//    }
//    @Provides
//    @Singleton
//    fun provideSettingsRepository(
//        @ApplicationContext context: Context
//    ): SettingsRepository {
//        return SettingsRepository(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserActivityRepository(
//        userActivityDao: UserActivityDao,
//        weatherRepository: WeatherRepository,
//        wateringRepository: WateringRepository,
//        plantRepository: PlantRepository
//    ): UserActivityRepository {
//        return UserActivityRepository(userActivityDao,weatherRepository,wateringRepository, plantRepository)
//    }
//
//
//    @Provides
//    @Singleton
//    fun provideAppRepository(
//        plantRepository: PlantRepository,
//        userActivityRepository: UserActivityRepository,
//        weatherRepository: WeatherRepository,
//        settingsRepository: SettingsRepository
//    ): AppRepository {
//        return AppRepository(plantRepository,userActivityRepository,weatherRepository,settingsRepository)
//    }
//}
//
//
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [WeatherServiceModule::class]
//)
//object TestWeatherServiceModule {
//    @Provides
//    @Singleton
//    fun provideWeatherService(): WeatherService = ObliviousWeatherService()
//}
//
//// returns no data
//class ObliviousWeatherService : WeatherService {
//    override suspend fun getWeather5Days(
//        lat: Double,
//        lon: Double
//    ): JsonObject {
//        val response = JsonObject()
//        val listArray = JsonArray()
//        response.add("list", listArray)
//        return response
//    }
//}