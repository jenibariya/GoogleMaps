package com.example.myapplication.mvvm

import com.example.myapplication.roomdatabase.SearchLocation
import com.example.myapplication.roomdatabase.SearchLocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchLocationRepository(private val searchLocationDao: SearchLocationDao) {

    suspend fun getAllSearchLocations(): List<SearchLocation> {
        return withContext(Dispatchers.IO) {
            searchLocationDao.getAllSearchLocations()
        }

    }

    suspend fun saveSearchLocation(searchLocation: SearchLocation) {
        withContext(Dispatchers.IO) {
            searchLocationDao.insert(searchLocation)
        }
    }

    suspend fun deleteSearchLocation(searchLocation: SearchLocation) {
        withContext(Dispatchers.IO) {
            searchLocationDao.delete(searchLocation)
        }
    }

    suspend fun updateSearchLocation(searchLocation: SearchLocation) {
        withContext(Dispatchers.IO) {
            searchLocationDao.update(searchLocation)
        }
    }
}

