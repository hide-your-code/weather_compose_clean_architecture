package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = "",
    @SerializedName("cod") val cod: Int? = 0,
    @SerializedName("coord") val coord: Coord? = null,
    @SerializedName("weather") val weatherItems: List<Weather>? = emptyList(),
    @SerializedName("base") val base: String? = "",
    @SerializedName("main") val main: Main? = null,
    @SerializedName("visibility") val visibility: Int? = 0,
    @SerializedName("wind") val wind: Wind? = null,
    @SerializedName("clouds") val clouds: Cloud? = null,
    @SerializedName("dt") val dt: Long? = 0L,
    @SerializedName("sys") val sys: Sys? = null,
    @SerializedName("timezone") val timezone: Int? = null
) : Model()
