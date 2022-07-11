package com.minhdtm.example.weapose.utils.factory

import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.data.model.CurrentWeather
import com.minhdtm.example.weapose.data.model.Hourly
import com.minhdtm.example.weapose.data.model.Weather
import com.minhdtm.example.weapose.domain.usecase.GetHourWeatherUseCase

object ModelDefault {
    fun latLng() = LatLng(0.0, 0.0)

    fun latLngHaNoi() = LatLng(21.028844836079177, 105.85215012120968)

    fun currentWeather() = CurrentWeather()

    fun hourWeather() = GetHourWeatherUseCase.Response(
        today = (0..23).toMutableList().map {
            val dt = (hourly().dt ?: 0L) + it * 60 * 60 * 1000
            hourly().copy(dt = dt)
        },
        tomorrow = (23..47).toMutableList().map {
            val dt = (hourly().dt ?: 0L) + it * 60 * 60 * 1000
            hourly().copy(dt = dt)
        },
    )

    fun weather() = Weather(
        id = 1,
        main = "main",
        description = "description",
        icon = "icon",
    )

    fun hourly() = Hourly(
        dt = 1655882985,
        temp = 38.0,
        feelsLike = 40.0,
        pressure = 70.0,
        humidity = 10,
        dewPoint = 10.0,
        uvi = 10.0,
        clouds = 10.0,
        visibility = 7,
        windSpeed = 20.0,
        windDeg = 10,
        windGust = 10.0,
        weather = listOf(weather()),
        pop = 10.0,
    )
}
