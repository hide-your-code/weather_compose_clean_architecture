package com.minhdtm.example.weapose.presentation.model.factory

import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.model.DayWeatherViewData

fun previewDayWeatherViewData() = DayWeatherViewData(
    dateTime = "Saturday, Jan 1",
    weatherDetail = "Rain",
    icon = R.drawable.ic_rain,
    maxTemp = 40.0,
    minTemp = 30.0,
    humidity = 80,
    sunset = "06:00",
    sunrise = "17:00",
    uvIndex = 8.0,
    windSpeed = "10",
)
