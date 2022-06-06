package com.minhdtm.example.weapose.domain.repositories

import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.data.model.CurrentWeather
import com.minhdtm.example.weapose.data.model.OneCallResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeatherByCity(city: String): Flow<CurrentWeather>

    fun getCurrentWeatherByLocation(latLng: LatLng): Flow<CurrentWeather>

    fun getHourWeather(latLng: LatLng): Flow<OneCallResponse>

    fun getSevenDaysWeather(latLng: LatLng): Flow<OneCallResponse>
}
