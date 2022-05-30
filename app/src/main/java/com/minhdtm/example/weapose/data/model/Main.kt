package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("temp") val temp: Double? = 0.0,
    @SerializedName("feels_like") val feelsLike: Double? = 0.0,
    @SerializedName("temp_min") val tempMin: Double? = 0.0,
    @SerializedName("temp_max") val tempMax: Double? = 0.0,
    @SerializedName("pressure") val pressure: Double? = 0.0,
    @SerializedName("humidity") val humidity: Int? = 0,
    @SerializedName("sea_level") val seaLevel: Double? = 0.0,
    @SerializedName("grnd_level") val grndLevel: Double? = 0.0,
) : Model()
