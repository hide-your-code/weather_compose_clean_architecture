package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("speed") val speed: Double? = 0.0,
    @SerializedName("deg") val deg: Double? = 0.0,
    @SerializedName("gust") val gust: Double? = 0.0,
) : Model()
