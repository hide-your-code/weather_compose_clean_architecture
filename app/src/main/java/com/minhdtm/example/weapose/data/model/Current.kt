package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("dt") val dt: Long? = 0L,
    @SerializedName("sunrise") val sunrise: Long? = 0L,
    @SerializedName("sunset") val sunset: Long? = 0L,
    @SerializedName("temp") val temp: Double? = 0.0,
    @SerializedName("feels_like") val feelsLike: Double? = 0.0,
    @SerializedName("pressure") val pressure: Int? = 0,
    @SerializedName("humidity") val humidity: Int? = 0,
    @SerializedName("dew_point") val dewPoint: Double? = 0.0,
    @SerializedName("uvi") val uvi: Double? = 0.0,
    @SerializedName("clouds") val clouds: Int? = 0,
    @SerializedName("visibility") val visibility: Int? = 0,
    @SerializedName("wind_speed") val windSpeed: Int? = 0,
    @SerializedName("wind_deg") val windDeg: Int? = 0,
    @SerializedName("weather") val weather: List<Weather>? = emptyList(),
) : Model()
