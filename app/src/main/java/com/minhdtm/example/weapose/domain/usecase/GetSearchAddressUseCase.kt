package com.minhdtm.example.weapose.domain.usecase

import com.minhdtm.example.weapose.data.local.room.HistorySearchAddressEntity
import com.minhdtm.example.weapose.domain.repositories.UserRepository
import com.minhdtm.example.weapose.domain.usecase.base.FlowUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchAddressUseCase @Inject constructor(
    @MainDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
) : FlowUseCase<Unit, List<HistorySearchAddressEntity>>(ioDispatcher) {
    override fun execute(params: Unit?): Flow<List<HistorySearchAddressEntity>> = userRepository.getSearchAddress()
}
