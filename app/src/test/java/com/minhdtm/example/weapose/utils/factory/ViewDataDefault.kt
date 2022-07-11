package com.minhdtm.example.weapose.utils.factory

import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData

object ViewDataDefault {
    fun currentWeather() = CurrentWeatherViewData(
        city = "Ha Noi",
        maxTemp = "40",
        minTemp = "30",
        temp = "35",
        weather = "Rain",
        sunRise = "06:00",
        wind = "20",
        humidity = "80",
        background = 0,
    )

    fun hourWeather() = HourWeatherViewData(
        timeStamp = 1655966766,
        time = "10:00",
        weatherIcon = 0,
    )

}
