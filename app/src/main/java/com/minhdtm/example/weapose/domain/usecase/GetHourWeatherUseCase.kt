package com.minhdtm.example.weapose.domain.usecase

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.data.model.Hourly
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.repositories.WeatherRepository
import com.minhdtm.example.weapose.domain.usecase.base.FlowUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import com.minhdtm.example.weapose.presentation.utils.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHourWeatherUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val weatherRepository: WeatherRepository,
) : FlowUseCase<GetHourWeatherUseCase.Params, GetHourWeatherUseCase.Response>(mainDispatcher) {
    override fun execute(params: Params?): Flow<Response> = if (params == null) {
        WeatherException.SnackBarException(message = context.getString(R.string.error_message_lat_lng_are_invalid))
            .asFlow()
    } else {
        weatherRepository.getHourWeather(params.latLng).map { response ->
            val firstTime = response.hourly?.firstOrNull()?.dt ?: 0L

            val today = response.hourly?.filter {
                it.dt != null && it.dt <= firstTime + oneDaySeconds()
            } ?: emptyList()

            val tomorrow = response.hourly?.filter {
                it.dt != null && it.dt > firstTime + oneDaySeconds() && it.dt <= firstTime + twoDaySeconds()
            } ?: emptyList()

            Response(today, tomorrow)
        }
    }

    private fun oneDaySeconds(): Long = TimeUnit.DAYS.toSeconds(1)

    private fun twoDaySeconds(): Long = oneDaySeconds() * 2

    data class Params(
        val latLng: LatLng,
    )

    data class Response(
        val today: List<Hourly>,
        val tomorrow: List<Hourly>,
    )
}
