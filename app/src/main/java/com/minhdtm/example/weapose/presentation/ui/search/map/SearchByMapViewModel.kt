package com.minhdtm.example.weapose.presentation.ui.search.map

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
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
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

    init {
        callApi {
            getDarkModeGoogleMapUseCase().collect { isDarkMode ->
                _state.update {
                    it.copy(isDarkMode = isDarkMode)
                }
            }
        }

        callApi {
            savedStateHandle.getStateFlow(Constants.Key.LAT, "").zip(
                savedStateHandle.getStateFlow(Constants.Key.LNG, ""),
                transform = { lat, lng ->
                    LatLng(lat.toDouble(), lng.toDouble())
                },
            ).collect { latLng ->
                if (latLng != Constants.Default.LAT_LNG_DEFAULT) {
                    setMarker(latLng)
                }
            }
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
            _state.update {
                it.copy(moveCamera = latLng)
            }

            getAddressFromLocationUseCase(GetAddressFromLocationUseCase.Params(latLng)).collect { address ->
                _state.update {
                    it.copy(
                        address = address,
                        marker = MarkerState(position = latLng),
                    )
                }
            }
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
            val toRoute = savedStateHandle.get<String>(Constants.Key.FROM_ROUTE)

            if (!toRoute.isNullOrBlank()) {
                _state.update {
                    it.copy(
                        popupToRoute = toRoute,
                    )
                }
            }
        }
    }

    fun cleanEvent() {
        _state.update {
            it.copy(
                popupToRoute = null,
                moveCamera = null,
            )
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(error = null)
        }
    }
}

data class SearchByMapViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val isDarkMode: Boolean = false,
    val marker: MarkerState? = null,
    val address: Address? = null,
    val popupToRoute: String? = null,
    val moveCamera: LatLng? = null,
) : ViewState(isLoading, error)
