package com.minhdtm.example.weapose.presentation.ui.infomation

import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(InformationViewState())
    val state: StateFlow<InformationViewState> = _state
}

data class InformationViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
) : ViewState(isLoading, error)
