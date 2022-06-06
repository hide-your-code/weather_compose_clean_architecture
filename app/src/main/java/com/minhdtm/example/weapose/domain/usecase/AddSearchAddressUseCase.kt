package com.minhdtm.example.weapose.domain.usecase

import android.content.Context
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.data.local.room.HistorySearchAddressEntity
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.repositories.UserRepository
import com.minhdtm.example.weapose.domain.usecase.base.SuspendUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSearchAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
) : SuspendUseCase<AddSearchAddressUseCase.Params, Unit>(ioDispatcher) {
    override suspend fun execute(params: Params?) {
        if (params != null) {
            userRepository.addSearchAddress(params.searchAddress)
        } else {
            throw WeatherException.SnackBarException(message = context.getString(R.string.error_message_default))
        }
    }

    data class Params(
        val searchAddress: HistorySearchAddressEntity
    )
}
