package com.codepath.articlesearch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "amount") val amount: Int,  // Water amount in ml
    @ColumnInfo(name = "date") val date: Long = Date().time
)