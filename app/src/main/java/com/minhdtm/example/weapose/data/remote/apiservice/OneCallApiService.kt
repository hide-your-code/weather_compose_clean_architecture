package com.minhdtm.example.weapose.data.remote.apiservice

import com.minhdtm.example.weapose.data.model.OneCallResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface OneCallApiService {
    @GET("onecall")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,daily,alerts,minutes",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") appId: String
    ): Flow<OneCallResponse>
}
