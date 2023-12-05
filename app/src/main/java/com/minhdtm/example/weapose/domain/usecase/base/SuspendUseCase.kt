package com.minhdtm.example.weapose.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class SuspendUseCase<in Params, out T>(private val coroutineDispatcher: CoroutineDispatcher) {

    protected abstract suspend fun execute(params: Params? = null): T

    suspend operator fun invoke(params: Params? = null): T = withContext(coroutineDispatcher) {
        execute(params)
    }
}
