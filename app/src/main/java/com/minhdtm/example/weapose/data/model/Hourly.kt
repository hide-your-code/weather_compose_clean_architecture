package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Hourly(
    @SerializedName("dt") val dt: Long? = 0L,
    @SerializedName("temp") val temp: Double? = 0.0,
    @SerializedName("feels_like") val feelsLike: Double? = 0.0,
    @SerializedName("pressure") val pressure: Double? = 0.0,
    @SerializedName("humidity") val humidity: Int? = 0,
    @SerializedName("dew_point") val dewPoint: Double? = 0.0,
    @SerializedName("uvi") val uvi: Double? = 0.0,
    @SerializedName("clouds") val clouds: Double? = 0.0,
    @SerializedName("visibility") val visibility: Int? = 0,
    @SerializedName("wind_speed") val windSpeed: Double? = 0.0,
    @SerializedName("wind_deg") val windDeg: Int? = 0,
    @SerializedName("wind_gust") val windGust: Double? = 0.0,
    @SerializedName("weather") val weather: List<Weather>? = emptyList(),
    @SerializedName("pop") val pop: Double? = 0.0,
) : Model()
