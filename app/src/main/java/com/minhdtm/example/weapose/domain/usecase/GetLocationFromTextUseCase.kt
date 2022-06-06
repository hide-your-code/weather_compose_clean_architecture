package com.minhdtm.example.weapose.domain.usecase

import android.content.Context
import android.location.Address
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.repositories.LocationRepository
import com.minhdtm.example.weapose.domain.usecase.base.FlowUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import com.minhdtm.example.weapose.presentation.utils.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationFromTextUseCase @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository,
) : FlowUseCase<GetLocationFromTextUseCase.Params, Address>(mainDispatcher) {
    override fun execute(params: Params?): Flow<Address> = if (params == null) {
        WeatherException.SnackBarException(message = context.getString(R.string.error_message_lat_lng_are_invalid))
            .asFlow()
    } else {
        locationRepository.getLocationFromText(params.text)
    }

    data class Params(
        val text: String
    )
}
