package com.minhdtm.example.weapose.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.model.AlertDialog
import com.minhdtm.example.weapose.domain.usecase.GetCurrentLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentWeatherUseCase
import com.minhdtm.example.weapose.domain.usecase.GetHourWeatherUseCase
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.Event
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherMapper
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.model.HourWeatherMapper
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // No leak in here!
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getHourWeatherUseCase: GetHourWeatherUseCase,
    private val hourWeatherMapper: HourWeatherMapper,
) : BaseViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    private val _event = Channel<HomeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private var currentLocation = Constants.Default.LAT_LNG_DEFAULT

    init {
        viewModelScope.launch {
            _event.send(HomeEvent.CheckPermission)
        }
    }

    fun getLocation(latLng: LatLng? = null) {
        callApi {
            showLoading()

            if (latLng == null) {
                getCurrentLocationUseCase().catch { exception ->
                    _state.update {
                        it.copy(
                            isRefresh = false,
                            isLoading = false,
                        )
                    }

                    throw exception
                }.collect {
                    currentLocation = it
                    getCurrentWeather(it)
                }
            } else {
                currentLocation = latLng
                getCurrentWeather(latLng)
            }
        }
    }

    private fun getCurrentWeather(latLng: LatLng) {
        callApi {
            getCurrentWeatherUseCase(GetCurrentWeatherUseCase.Params(latLng)).zip(
                getHourWeatherUseCase(GetHourWeatherUseCase.Params(latLng)),
                transform = { currentWeather, hourWeather ->
                    HomeViewState(
                        currentWeather = currentWeatherMapper.mapToViewData(currentWeather),
                        listHourlyWeatherToday = hourWeather.today.map { hourly ->
                            hourWeatherMapper.mapToViewData(hourly)
                        },
                    )
                },
            ).collect { viewState ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefresh = false,
                        currentWeather = viewState.currentWeather,
                        listHourlyWeatherToday = viewState.listHourlyWeatherToday,
                    )
                }
            }
        }
    }

    fun permissionIsNotGranted() {
        val error = WeatherException.AlertException(
            code = -1, alertDialog = AlertDialog(
                title = context.getString(R.string.error_title_permission_not_granted),
                message = context.getString(R.string.error_message_permission_not_granted),
                positiveMessage = "Open setting",
                negativeMessage = context.getString(android.R.string.cancel),
                positiveAction = ActionType.OPEN_PERMISSION,
            )
        )
        showError(error)
    }

    fun navigateToSearchByMap() {
        callApi {
            _event.send(HomeEvent.NavigateToSearchByMap(latLng = currentLocation))
        }
    }

    fun onRefreshCurrentWeather() {
        _state.update {
            it.copy(
                isRefresh = true
            )
        }

        getLocation(currentLocation)
    }

    private fun showLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    private fun hideLoading() {
        _state.update {
            it.copy(isLoading = false)
        }
    }

    override fun showError(error: WeatherException) {
        if (_state.value.error == null) {
            _state.update {
                it.copy(isLoading = false, error = error)
            }
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(isLoading = false, error = null)
        }
    }
}

data class HomeViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val isRefresh: Boolean = false,
    val currentWeather: CurrentWeatherViewData? = null,
    val listHourlyWeatherToday: List<HourWeatherViewData> = emptyList(),
) : ViewState(isLoading, error)

sealed class HomeEvent : Event() {
    object CheckPermission : HomeEvent()

    data class NavigateToSearchByMap(val latLng: LatLng) : HomeEvent()
}
