package com.minhdtm.example.weapose.presentation.ui.search.bymap

import android.location.Address
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.usecase.GetAddressFromLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetDarkModeGoogleMapUseCase
import com.minhdtm.example.weapose.domain.usecase.SetDarkModeGoogleMapUseCase
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.Event
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchByMapViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val setDarkModeGoogleMapUseCase: SetDarkModeGoogleMapUseCase,
    private val getDarkModeGoogleMapUseCase: GetDarkModeGoogleMapUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SearchByMapViewState())
    val state: StateFlow<SearchByMapViewState> = _state

    private val _event = Channel<SearchByMapEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        callApi {
            val getDarkMode = async {
                getDarkModeGoogleMapUseCase().collect { isDarkMode ->
                    _state.update {
                        it.copy(isDarkMode = isDarkMode)
                    }
                }
            }

            val getDataFromPreviousScreen = async {
                savedStateHandle.getStateFlow(Constants.Key.LAT, "").zip(
                    savedStateHandle.getStateFlow(Constants.Key.LNG, ""),
                    transform = { lat, lng ->
                        Pair(lat, lng)
                    },
                ).collect { latAndLng ->
                    val latLng = LatLng(latAndLng.first.toDouble(), latAndLng.second.toDouble())
                    if (latLng != Constants.Default.LAT_LNG_DEFAULT) {
                        setMarker(latLng)
                    }
                }
            }

            getDarkMode.await()
            getDataFromPreviousScreen.await()
        }
    }

    fun setDarkMode() {
        callApi {
            val isDarkMode = !_state.value.isDarkMode
            setDarkModeGoogleMapUseCase(SetDarkModeGoogleMapUseCase.Params(isDarkMode))
        }
    }

    fun setMarker(latLng: LatLng) {
        callApi {
            _event.send(SearchByMapEvent.MoveCamera(latLng))

            getAddressFromLocationUseCase(GetAddressFromLocationUseCase.Params(latLng)).catch {
                _state.update {
                    it.copy(
                        address = null,
                        marker = MarkerState(position = latLng),
                    )
                }
            }.collect { address ->
                _state.update {
                    it.copy(
                        address = address,
                        marker = MarkerState(position = latLng),
                    )
                }
            }
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(error = null)
        }
    }

    fun onClickCurrentLocation() {
        callApi {
            getCurrentLocationUseCase().collect { latLng ->
                setMarker(latLng)
            }
        }
    }

    fun onClickDone() {
        callApi {
            val lat = _state.value.address?.latitude
            val lng = _state.value.address?.longitude

            if (lat != null && lng != null) {
                val latLng = LatLng(lat, lng)
                _event.send(SearchByMapEvent.PopToHome(latLng = latLng))
            }
        }
    }
}

data class SearchByMapViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val isDarkMode: Boolean = false,
    val marker: MarkerState? = null,
    val address: Address? = null,
) : ViewState(isLoading, error)

sealed class SearchByMapEvent : Event() {
    data class PopToHome(val latLng: LatLng) : SearchByMapEvent()

    data class MoveCamera(val latLng: LatLng) : SearchByMapEvent()
}
