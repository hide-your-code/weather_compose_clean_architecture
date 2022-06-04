package com.minhdtm.example.weapose.domain.usecase

import android.content.Context
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.repositories.LocationRepository
import com.minhdtm.example.weapose.domain.usecase.base.FlowUseCase
import com.minhdtm.example.weapose.presentation.di.IoDispatcher
import com.minhdtm.example.weapose.presentation.utils.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAddressFromTextUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val locationRepository: LocationRepository,
) : FlowUseCase<GetAddressFromTextUseCase.Params, List<AutocompletePrediction>>(ioDispatcher) {
    override fun execute(params: Params?): Flow<List<AutocompletePrediction>> = if (params == null) {
        WeatherException.SnackBarException(message = context.getString(R.string.error_message_address_is_not_found))
            .asFlow()
    } else {
        locationRepository.getAddress(params.text)
    }

    data class Params(
        val text: String,
    )
}
