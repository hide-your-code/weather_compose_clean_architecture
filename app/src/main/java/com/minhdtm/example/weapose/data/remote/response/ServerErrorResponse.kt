package com.minhdtm.example.weapose.data.remote.response

import com.google.gson.annotations.SerializedName

data class ServerErrorResponse(
    @SerializedName("cod") val code: Int? = 0,
    @SerializedName("message") val message: String? = "",
)
