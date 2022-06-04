package com.minhdtm.example.weapose.data.di

import android.content.Context
import androidx.room.Room
import com.minhdtm.example.weapose.data.local.room.HistorySearchAddressDao
import com.minhdtm.example.weapose.data.local.room.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context,
    ): WeatherDatabase =
        Room.databaseBuilder(context, WeatherDatabase::class.java, WeatherDatabase.database_name).build()

    @Provides
    @Singleton
    fun provideSearchWeatherDao(
        weatherDatabase: WeatherDatabase
    ): HistorySearchAddressDao = weatherDatabase.historySearchAddressDao()
}
