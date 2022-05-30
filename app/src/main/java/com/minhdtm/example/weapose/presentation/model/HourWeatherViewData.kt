package com.minhdtm.example.weapose.presentation.model

import com.minhdtm.example.weapose.data.model.Hourly
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.toDateTime
import com.minhdtm.example.weapose.presentation.utils.toIcon
import javax.inject.Inject

data class HourWeatherViewData(
    val timeStamp: Long,
    val time: String,
    val weatherIcon: Int,
) : ViewData()

class HourWeatherMapper @Inject constructor() : DataModelMapper<Hourly, HourWeatherViewData> {
    override fun mapToModel(viewData: HourWeatherViewData): Hourly {
        TODO("Not yet implemented")
    }

    override fun mapToViewData(model: Hourly): HourWeatherViewData {
        val timeStamp = model.dt ?: 0L
        val time = model.dt?.times(1000L)?.toDateTime(Constants.DateFormat.HH_mm) ?: ""
        val weatherIcon = (model.weather?.firstOrNull()?.icon ?: "").toIcon()

        return HourWeatherViewData(timeStamp = timeStamp, time = time, weatherIcon = weatherIcon)
    }
}
