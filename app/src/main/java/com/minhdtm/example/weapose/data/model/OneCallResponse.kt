package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class OneCallResponse(
    @SerializedName("lat") val lat: Double? = 0.0,
    @SerializedName("lon") val long: Double? = 0.0,
    @SerializedName("time_zone") val timeZone: String? = "",
    @SerializedName("timezone_offset") val timeZoneOffSet: Int? = 0,
    @SerializedName("current") val current: Current? = null,
    @SerializedName("hourly") val hourly: List<Hourly>? = emptyList(),
    @SerializedName("daily") val daily: List<Daily>? = emptyList(),
) : Model()
