package com.minhdtm.example.weapose.presentation.ui.search.text

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.usecase.*
import com.minhdtm.example.weapose.presentation.base.BaseViewModel
import com.minhdtm.example.weapose.presentation.base.Event
import com.minhdtm.example.weapose.presentation.base.ViewState
import com.minhdtm.example.weapose.presentation.model.HistorySearchAddressViewData
import com.minhdtm.example.weapose.presentation.model.HistorySearchAddressViewDataMapper
import com.minhdtm.example.weapose.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SearchByTextViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // No leak in here! Don't worry about that.
    private val savedStateHandle: SavedStateHandle,
    private val getAddressFromTextUseCase: GetAddressFromTextUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
    private val addSearchAddressUseCase: AddSearchAddressUseCase,
    private val getSearchAddressUseCase: GetSearchAddressUseCase,
    private val historySearchAddressViewDataMapper: HistorySearchAddressViewDataMapper,
    private val clearAllSearchAddressUseCase: ClearAllSearchAddressUseCase,
) : BaseViewModel() {
    private val placeHolder: MutableList<String> =
        mutableListOf(context.getString(R.string.search_every_where_you_want))

    private val _state = MutableStateFlow(SearchByTextViewState(addressPlaceHolder = placeHolder))
    val state: StateFlow<SearchByTextViewState> = _state

    private val _event = Channel<SearchByTextEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        callApi {
            getSearchAddressUseCase().collect { listSearch ->
                _state.update {
                    it.copy(
                        listSearch = listSearch.take(10).map { search ->
                            historySearchAddressViewDataMapper.mapToViewData(search)
                        },
                    )
                }
            }
        }

        callApi {
            getCurrentLocationUseCase().flatMapConcat { latLng ->
                getAddressFromLocationUseCase(GetAddressFromLocationUseCase.Params(latLng))
            }.collect { address ->
                _state.update {
                    val latLng = LatLng(address.latitude, address.longitude)
                    it.copy(
                        currentLatLng = latLng,
                        addressPlaceHolder = placeHolder.apply { add(address.getAddressLine(0)) },
                    )
                }
            }
        }
    }

    fun updatePlaceHolder(default: String) {
        val newPlaceHolder = placeHolder.apply {
            set(0, default)
        }
        _state.update {
            it.copy(
                addressPlaceHolder = newPlaceHolder
            )
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

    fun getAddress(text: String) {
        if (text.isBlank()) {
            _state.update {
                it.copy(listResult = emptyList())
            }
        } else {
            callApi {
                getAddressFromTextUseCase(GetAddressFromTextUseCase.Params(text)).collect { listAddress ->
                    _state.update { state ->
                        state.copy(
                            listResult = listAddress,
                        )
                    }
                }
            }
        }
    }

    fun addSearchHistory(address: String) {
        callApi {
            val entity = historySearchAddressViewDataMapper.mapToModel(HistorySearchAddressViewData(address))

            addSearchAddressUseCase.invoke(AddSearchAddressUseCase.Params(entity))
        }
    }

    fun clearHistory() {
        callApi {
            clearAllSearchAddressUseCase.invoke()
        }
    }

    fun onNavigateToSearchByMap() {
        callApi {
            var latLng = Constants.Default.LAT_LNG_DEFAULT

            val fromRoute = savedStateHandle.get<String>(Constants.Key.FROM_ROUTE) ?: ""
            val lat = savedStateHandle.get<String>(Constants.Key.LAT) ?: ""
            val lng = savedStateHandle.get<String>(Constants.Key.LNG) ?: ""

            if (lat.isNotBlank() && lng.isNotBlank()) {
                latLng = LatLng(lat.toDouble(), lng.toDouble())
            }

            _event.send(SearchByTextEvent.NavigateToSearchByMap(fromRoute, latLng))
        }
    }
}

data class SearchByTextViewState(
    override val isLoading: Boolean = false,
    override val error: WeatherException? = null,
    val currentLatLng: LatLng? = null,
    val listSearch: List<HistorySearchAddressViewData> = emptyList(),
    val addressPlaceHolder: List<String> = emptyList(),
    val listResult: List<AutocompletePrediction> = emptyList(),
) : ViewState(isLoading, error)

sealed class SearchByTextEvent : Event() {
    data class NavigateToSearchByMap(val fromRoute: String, val latLng: LatLng) : SearchByTextEvent()
}
