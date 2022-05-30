package com.minhdtm.example.weapose.data.model

import com.google.gson.annotations.SerializedName

data class Cloud(
    @SerializedName("all") val all: Int? = 0,
) : Model()
