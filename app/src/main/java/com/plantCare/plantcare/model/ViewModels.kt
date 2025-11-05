package com.plantCare.plantcare.model
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class PlantViewModel(private val dao: PlantDao) : ViewModel() {
//    fun insertPlant(plant: Plant) = viewModelScope.launch(Dispatchers.IO) {
//        dao.insertPlant(plant)
//    }
//}


object DatabaseClient {
    private var _db: AppDatabase? = null
    fun getDao(context: Context): TestDao {
        if (_db == null) {
            _db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "your_db_name"
            ).build()
        }
        return _db!!.testDao()
    }
}

class TestViewModel(private val testDao: TestDao) : ViewModel() {
    fun insertTest(name: String) {
        viewModelScope.launch {
            testDao.insertTest(TestTable(name = name))
        }
    }
}