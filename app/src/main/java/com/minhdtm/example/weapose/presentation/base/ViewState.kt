package com.minhdtm.example.weapose.presentation.base

import com.minhdtm.example.weapose.domain.exception.WeatherException

open class ViewState(
    open val isLoading: Boolean = false,
    open val error: WeatherException? = null,
)
