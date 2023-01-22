package com.minhdtm.example.weapose.presentation.ui.settings

import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(SettingViewState(isLoading = false))
    val state: StateFlow<SettingViewState> = _state

    init {
        retryViewModelScope {
            _state.update {
                it.copy(error = WeatherException.SnackBarException(message = "Test message!"))
            }
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(
                isLoading = false,
                error = null,
            )
        }
    }
}

data class SettingViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
) : ViewState()
