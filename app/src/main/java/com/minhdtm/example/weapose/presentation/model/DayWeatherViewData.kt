package com.minhdtm.example.weapose.presentation.model

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.minhdtm.example.weapose.data.model.Daily
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.toDateTime
import com.minhdtm.example.weapose.presentation.utils.toIcon
import java.util.*
import javax.inject.Inject

data class DayWeatherViewData(
    val dateTime: String,
    val weatherDetail: String,
    val icon: Int,
    val maxTemp: Double,
    val minTemp: Double,
    val windSpeed: String,
    val humidity: Int,
    val uvIndex: Double,
    val sunrise: String,
    val sunset: String,
) : ViewData()

class SevenWeatherViewDataMapper @Inject constructor() : DataModelMapper<Daily, DayWeatherViewData> {
    override fun mapToModel(viewData: DayWeatherViewData): Daily {
        TODO("Not yet implemented")
    }

    override fun mapToViewData(model: Daily): DayWeatherViewData {
        val dateTime = model.dt?.times(1000)?.toDateTime(Constants.DateFormat.EE_MM_dd) ?: ""
        val icon = (model.weather?.firstOrNull()?.icon ?: "").toIcon()
        val weatherDetail = model.weather?.firstOrNull()?.description?.capitalize(Locale.current) ?: ""
        val maxTemp = model.temp?.max ?: 0.0
        val minTemp = model.temp?.min ?: 0.0
        val windSpeed = String.format("%.1f", model.windSpeed?.times(3.6) ?: 0.0)
        val humidity = model.humidity ?: 0
        val uvIndex = model.uvi ?: 0.0
        val sunrise = model.sunrise?.times(1000)?.toDateTime(Constants.DateFormat.HH_mm) ?: ""
        val sunset = model.sunset?.times(1000)?.toDateTime(Constants.DateFormat.HH_mm) ?: ""


        return DayWeatherViewData(
            dateTime = dateTime,
            icon = icon,
            weatherDetail = weatherDetail,
            maxTemp = maxTemp,
            minTemp = minTemp,
            windSpeed = windSpeed,
            humidity = humidity,
            uvIndex = uvIndex,
            sunrise = sunrise,
            sunset = sunset,
        )
    }
}
