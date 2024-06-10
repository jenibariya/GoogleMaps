package com.example.myapplication.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchLocationDao {
    @Query("SELECT * FROM searchLocations")
    suspend fun getAllSearchLocations(): List<SearchLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchLocation: SearchLocation)

    @Delete
    suspend fun delete(searchLocation: SearchLocation)

    @Update
    suspend fun update(searchLocation: SearchLocation)
}

