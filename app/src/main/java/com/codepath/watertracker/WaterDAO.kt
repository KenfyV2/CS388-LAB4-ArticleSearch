package com.codepath.watertracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {
    @Query("SELECT * FROM water_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<WaterEntry>>

    @Insert
    suspend fun insert(waterEntry: WaterEntry)

    @Query("SELECT AVG(amount) FROM water_entries")
    suspend fun getAverageWaterIntake(): Double
}