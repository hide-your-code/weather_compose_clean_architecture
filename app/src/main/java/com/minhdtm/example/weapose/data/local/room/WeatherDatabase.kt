package com.minhdtm.example.weapose.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistorySearchAddressEntity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun historySearchAddressDao(): HistorySearchAddressDao

    companion object {
        const val database_name = "weather_database"
    }
}
