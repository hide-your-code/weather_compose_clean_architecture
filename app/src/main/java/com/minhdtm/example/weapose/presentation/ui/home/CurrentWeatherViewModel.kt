package com.minhdtm.example.weapose.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.model.AlertDialog
import com.minhdtm.example.weapose.domain.usecase.GetCurrentLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentWeatherUseCase
import com.minhdtm.example.weapose.domain.usecase.GetHourWeatherUseCase
import com.minhdtm.example.weapose.domain.usecase.GetLocationFromTextUseCase
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherMapper
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.model.HourWeatherMapper
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // No leak in here!
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getHourWeatherUseCase: GetHourWeatherUseCase,
    private val hourWeatherMapper: HourWeatherMapper,
    private val getLocationFromTextUseCase: GetLocationFromTextUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CurrentWeatherViewState())
    val state: StateFlow<CurrentWeatherViewState> = _state

    private var currentLocation = Constants.Default.LAT_LNG_DEFAULT

    init {
        _state.update {
            it.copy(isRequestPermission = true)
        }
    }

    fun getWeatherByAddressName(addressName: String) {
        callApi {
            showLoading()
            getLocationFromTextUseCase(GetLocationFromTextUseCase.Params(addressName)).collect {
                val latLng = LatLng(it.latitude, it.longitude)
                getCurrentWeather(latLng)
            }
        }
    }

    fun getWeatherByLocation(latLng: LatLng) {
        callApi {
            showLoading()
            getCurrentWeather(latLng)
        }
    }

    fun getCurrentLocation() {
        callApi {
            showLoading()
            getCurrentLocationUseCase().collect {
                getCurrentWeather(it)
            }
        }
    }

    private fun getCurrentWeather(latLng: LatLng) {
        callApi {
            if (currentLocation != latLng) {
                currentLocation = latLng
            }

            getCurrentWeatherUseCase(GetCurrentWeatherUseCase.Params(currentLocation)).zip(
                getHourWeatherUseCase(GetHourWeatherUseCase.Params(currentLocation)),
                transform = { currentWeather, hourWeather ->
                    CurrentWeatherViewState(
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
        _state.update {
            it.copy(navigateSearch = currentLocation)
        }
    }

    fun onRefreshCurrentWeather(showRefresh: Boolean = true) {
        _state.update {
            it.copy(
                isRefresh = showRefresh,
                isLoading = !showRefresh,
            )
        }

        getCurrentWeather(currentLocation)
    }

    fun cleanEvent() {
        _state.update {
            it.copy(
                isRequestPermission = false,
                navigateSearch = null,
            )
        }
    }

    override fun hideLoading() {
        _state.update {
            it.copy(
                isLoading = false,
                isRefresh = false,
            )
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

    private fun showLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }
}

data class CurrentWeatherViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val isRefresh: Boolean = false,
    val currentWeather: CurrentWeatherViewData? = null,
    val listHourlyWeatherToday: List<HourWeatherViewData> = emptyList(),
    val navigateSearch: LatLng? = null,
    val isRequestPermission: Boolean = false,
) : ViewState(isLoading, error)
