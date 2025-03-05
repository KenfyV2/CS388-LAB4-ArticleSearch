package com.codepath.articlesearch

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WaterEntry::class], version = 1)
abstract class WaterTrackingDatabase : RoomDatabase() {
    abstract fun waterEntryDao(): WaterEntryDao

    companion object {
        @Volatile
        private var INSTANCE: WaterTrackingDatabase? = null

        fun getDatabase(context: Context): WaterTrackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterTrackingDatabase::class.java,
                    "water_tracking_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}