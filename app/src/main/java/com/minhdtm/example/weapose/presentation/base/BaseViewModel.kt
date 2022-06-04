package com.minhdtm.example.weapose.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhdtm.example.weapose.domain.exception.WeatherException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception.message)
        hideLoading()
        val errorResponse = if (exception is WeatherException) {
            exception
        } else {
            WeatherException.SnackBarException(message = exception.message ?: "")
        }

        showError(errorResponse)
    }

    private var job: Job? = null

    private var callApi: suspend CoroutineScope.() -> Unit = {}

    open fun showError(error: WeatherException) {}

    open fun hideError() {}

    open fun hideLoading() {}

    fun callApi(api: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            callApi = api

            job = launch {
                callApi.invoke(this)
            }

            job?.join()
        }
    }

    open fun retry() {
        viewModelScope.launch(coroutineExceptionHandler) {
            job?.cancel()
            job = launch {
                callApi.invoke(this)
            }
            job?.join()
        }
    }

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }
}
