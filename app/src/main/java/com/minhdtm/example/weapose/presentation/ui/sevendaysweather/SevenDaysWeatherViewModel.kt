package com.minhdtm.example.weapose.presentation.ui.sevendaysweather

import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.usecase.GetAddressFromLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentAddressUseCase
import com.minhdtm.example.weapose.domain.usecase.GetLocationFromTextUseCase
import com.minhdtm.example.weapose.domain.usecase.GetSevenDaysWeatherUseCase
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.model.DayWeatherViewData
import com.minhdtm.example.weapose.presentation.model.SevenWeatherViewDataMapper
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SevenDaysWeatherViewModel @Inject constructor(
    private val getCurrentAddressUseCase: GetCurrentAddressUseCase,
    private val getSevenDaysWeatherUseCase: GetSevenDaysWeatherUseCase,
    private val sevenWeatherViewDataMapper: SevenWeatherViewDataMapper,
    private val getLocationFromTextUseCase: GetLocationFromTextUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SevenDaysViewState(isLoading = true))
    val state: StateFlow<SevenDaysViewState> = _state

    private var currentLocation = Constants.Default.LAT_LNG_DEFAULT

    init {
        getCurrentWeather()
    }

    fun getWeatherByAddressName(addressName: String) {
        callApi {
            showLoading()

            _state.update {
                it.copy(address = addressName)
            }

            getLocationFromTextUseCase(GetLocationFromTextUseCase.Params(addressName)).collect { address ->
                val latLng = LatLng(address.latitude, address.longitude)
                getSevenDaysWeather(latLng)
            }
        }
    }

    fun getWeatherByLocation(latLng: LatLng) {
        callApi {
            showLoading()

            getAddressFromLocationUseCase.invoke(GetAddressFromLocationUseCase.Params(latLng)).collect { address ->
                _state.update {
                    it.copy(
                        address = address.featureName
                    )
                }

                getSevenDaysWeather(latLng)
            }
        }
    }

    private fun getCurrentWeather() {
        callApi {
            showLoading()

            getCurrentAddressUseCase().collect { address ->
                _state.update {
                    it.copy(
                        address = address.featureName
                    )
                }

                val latLng = LatLng(address.latitude, address.longitude)
                getSevenDaysWeather(latLng)
            }
        }
    }

    private fun getSevenDaysWeather(latLng: LatLng) {
        callApi {
            currentLocation = latLng

            getSevenDaysWeatherUseCase(GetSevenDaysWeatherUseCase.Params(latLng)).collect { response ->
                val listSevenDays = response.daily?.map { sevenWeatherViewDataMapper.mapToViewData(it) }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefresh = false,
                        listSevenDays = listSevenDays ?: emptyList(),
                    )
                }
            }
        }
    }

    fun onNavigateToSearch() {
        _state.update {
            it.copy(navigateToSearchByText = currentLocation)
        }
    }

    fun onRefresh(isShowRefresh: Boolean = true) {
        callApi {
            _state.update {
                it.copy(
                    isRefresh = isShowRefresh,
                    isLoading = false,
                )
            }

            getSevenDaysWeather(currentLocation)
        }
    }

    override fun showError(error: WeatherException) {
        if (_state.value.error == null) {
            _state.update {
                it.copy(isLoading = false, error = error)
            }
        }
    }

    private fun showLoading() {
        _state.update {
            it.copy(
                isLoading = true,
                listSevenDays = emptyList(),
            )
        }
    }

    override fun hideLoading() {
        _state.update {
            it.copy(isLoading = false)
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(isLoading = false, error = null)
        }
    }

    fun cleanEvent() {
        _state.update {
            it.copy(
                navigateToSearchByText = null,
            )
        }
    }
}

data class SevenDaysViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val isRefresh: Boolean = false,
    val address: String = "",
    val listSevenDays: List<DayWeatherViewData> = emptyList(),
    val navigateToSearchByText: LatLng? = null,
) : ViewState(isLoading, error)
