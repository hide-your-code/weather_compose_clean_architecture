package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Temp(
    @SerializedName("day") val dt: Double? = 0.0,
    @SerializedName("min") val min: Double? = 0.0,
    @SerializedName("max") val max: Double? = 0.0,
    @SerializedName("night") val night: Double? = 0.0,
    @SerializedName("eve") val eve: Double? = 0.0,
    @SerializedName("morn") val morn: Double? = 0.0,
) : Model()
