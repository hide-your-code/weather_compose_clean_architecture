package com.minhdtm.example.weapose.presentation.model.factory

import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData

fun previewCurrentWeatherViewData() = CurrentWeatherViewData(
    city = "Ha Noi",
    maxTemp = "40",
    minTemp = "30",
    temp = "35",
    weather = "Sunny",
    sunRise = "6:00",
    wind = "10",
    humidity = "70",
    background = R.drawable.bg_clear_sky
)
