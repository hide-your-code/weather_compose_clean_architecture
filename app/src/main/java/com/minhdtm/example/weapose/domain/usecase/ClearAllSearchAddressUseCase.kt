package com.minhdtm.example.weapose.domain.usecase

import com.minhdtm.example.weapose.domain.repositories.UserRepository
import com.minhdtm.example.weapose.domain.usecase.base.SuspendUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearAllSearchAddressUseCase @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
) : SuspendUseCase<Unit, Unit>(mainDispatcher) {
    override suspend fun execute(params: Unit?) = userRepository.clearAllSearchAddress()
}
