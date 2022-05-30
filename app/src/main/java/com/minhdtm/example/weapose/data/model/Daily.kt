package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("dt") val dt: Long? = 0L,
    @SerializedName("sunrise") val sunrise: Long? = 0L,
    @SerializedName("sunset") val sunset: Long? = 0L,
    @SerializedName("moonrise") val moonrise: Long? = 0L,
    @SerializedName("moonset") val moonset: Long? = 0L,
    @SerializedName("moon_phase") val moonPhase: Double? = 0.0,
    @SerializedName("temp") val temp: Temp? = null,
    @SerializedName("feels_like") val feelsLike: FeelLike? = null,
    @SerializedName("pressure") val pressure: Int? = 0,
    @SerializedName("humidity") val humidity: Int? = 0,
    @SerializedName("dew_point") val dewPoint: Double? = 0.0,
    @SerializedName("wind_speed") val windSpeed: Double? = 0.0,
    @SerializedName("wind_deg") val windDeg: Double? = 0.0,
    @SerializedName("wind_gust") val windGust: Double? = 0.0,
    @SerializedName("weather") val weather: List<Weather>? = emptyList(),
    @SerializedName("clouds") val clouds: Int? = 0,
    @SerializedName("pop") val pop: Int? = 0,
    @SerializedName("uvi") val uvi: Double? = 0.0,
) : Model()
