package com.minhdtm.example.weapose.data.repositories

import androidx.compose.ui.text.intl.Locale
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.BuildConfig
import com.minhdtm.example.weapose.data.model.CurrentWeather
import com.minhdtm.example.weapose.data.model.OneCallResponse
import com.minhdtm.example.weapose.data.remote.apiservice.CurrentWeatherApiService
import com.minhdtm.example.weapose.data.remote.apiservice.OneCallApiService
import com.minhdtm.example.weapose.domain.repositories.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val currentWeatherApiService: CurrentWeatherApiService,
    private val oneCallApiService: OneCallApiService,
) : WeatherRepository {
    override fun getCurrentWeatherByCity(city: String): Flow<CurrentWeather> =
        currentWeatherApiService.getCurrentWeatherByCity(
            city = city,
            lang = Locale.current.language,
            appId = BuildConfig.API_KEY,
        )

    override fun getCurrentWeatherByLocation(latLng: LatLng): Flow<CurrentWeather> =
        currentWeatherApiService.getCurrentWeatherByLocation(
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            lang = Locale.current.language,
            appId = BuildConfig.API_KEY,
        )

    override fun getHourWeather(latLng: LatLng): Flow<OneCallResponse> = oneCallApiService.getWeather(
        lat = latLng.latitude,
        lon = latLng.longitude,
        lang = Locale.current.language,
        appId = BuildConfig.API_KEY,
    )
}
