package com.minhdtm.example.weapose.data.di

import android.content.Context
import com.minhdtm.example.weapose.data.local.datastore.PreferenceStorage
import com.minhdtm.example.weapose.data.remote.apiservice.CurrentWeatherApiService
import com.minhdtm.example.weapose.data.remote.apiservice.OneCallApiService
import com.minhdtm.example.weapose.data.repositories.LocationRepositoryImpl
import com.minhdtm.example.weapose.data.repositories.UserRepositoryImpl
import com.minhdtm.example.weapose.data.repositories.WeatherRepositoryImpl
import com.minhdtm.example.weapose.domain.repositories.LocationRepository
import com.minhdtm.example.weapose.domain.repositories.UserRepository
import com.minhdtm.example.weapose.domain.repositories.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideWeatherRepository(
        currentWeatherApiService: CurrentWeatherApiService,
        oneCallApiService: OneCallApiService,
    ): WeatherRepository = WeatherRepositoryImpl(currentWeatherApiService, oneCallApiService)

    @Provides
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository =
        LocationRepositoryImpl(context)

    @Provides
    fun provideUserRepository(preferenceStorage: PreferenceStorage): UserRepository =
        UserRepositoryImpl(preferenceStorage)
}
