package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("type") val type: Int? = 0,
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("country") val country: String? = "",
    @SerializedName("sunrise") val sunrise: Long? = 0L,
    @SerializedName("sunset") val sunset: Long? = 0L,
) : Model()
