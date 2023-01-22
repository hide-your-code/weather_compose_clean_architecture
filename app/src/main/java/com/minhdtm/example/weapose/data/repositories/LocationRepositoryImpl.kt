package com.minhdtm.example.weapose.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.repositories.LocationRepository
import com.minhdtm.example.weapose.presentation.utils.getSystemLocale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val token: AutocompleteSessionToken,
    private val placesClient: PlacesClient,
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<LatLng> = flow {
        // Just want to get location only want.
        // If you want to emit the value every time `addOnSuccessListener()` return, you could using `callBackFlow` instead `suspendCancelableCoroutine`.
        emit(suspendCancellableCoroutine { cancellableContinuation ->
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            ).addOnSuccessListener { location ->
                if (location != null) {
                    cancellableContinuation.resume(LatLng(location.latitude, location.longitude))
                } else {
                    val error = WeatherException.SnackBarException(
                        message = context.getString(R.string.error_message_current_location_is_null)
                    )
                    cancellableContinuation.resumeWithException(error)
                }
            }.addOnFailureListener { exception ->
                val error = WeatherException.SnackBarException(message = exception.message ?: "")
                cancellableContinuation.resumeWithException(error)
            }.addOnCompleteListener {
                cancellableContinuation.cancel()
            }.addOnCanceledListener {
                cancellableContinuation.cancel()
            }

            cancellableContinuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        })
    }

    @OptIn(FlowPreview::class)
    @Suppress("DEPRECATION")
    override fun getCurrentAddress(): Flow<Address> = getCurrentLocation().flatMapConcat { latLng ->
        flow {
            emit(suspendCancellableCoroutine { cancellableContinuation ->
                val error = WeatherException.SnackBarException(
                    message = context.getString(R.string.error_message_current_address_is_not_found)
                )

                val getSystemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales[0]
                } else {
                    context.resources.configuration.locale
                }

                val geo = Geocoder(context, getSystemLocale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geo.getFromLocation(latLng.latitude, latLng.longitude, 1) { listAddress ->
                        if (listAddress.isEmpty()) {
                            cancellableContinuation.resumeWithException(error)
                        } else {
                            cancellableContinuation.resume(listAddress.first())
                        }
                        cancellableContinuation.cancel()
                    }
                } else {
                    val listAddress = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (listAddress.isNullOrEmpty()) {
                        cancellableContinuation.resumeWithException(error)
                    } else {
                        cancellableContinuation.resume(listAddress.first())
                    }
                    cancellableContinuation.cancel()
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    override fun getLocationFromText(text: String): Flow<Address> = flow {
        emit(suspendCancellableCoroutine { cancellableContinuation ->
            val error = WeatherException.SnackBarException(
                message = context.getString(R.string.error_message_current_address_is_not_found)
            )

            val getSystemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                context.resources.configuration.locale
            }

            val geo = Geocoder(context, getSystemLocale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geo.getFromLocationName(text, 1) { listAddress ->
                    if (listAddress.isEmpty()) {
                        cancellableContinuation.resumeWithException(error)
                    } else {
                        cancellableContinuation.resume(listAddress.first())
                    }
                    cancellableContinuation.cancel()
                }
            } else {
                val listAddress = geo.getFromLocationName(text, 1)
                if (listAddress.isNullOrEmpty()) {
                    cancellableContinuation.resumeWithException(error)
                } else {
                    cancellableContinuation.resume(listAddress.first())
                }
                cancellableContinuation.cancel()
            }
        })
    }

    @Suppress("DEPRECATION")
    override fun getAddressFromLocation(latLng: LatLng): Flow<Address> = flow {
        emit(suspendCancellableCoroutine { cancellableContinuation ->
            val error = WeatherException.SnackBarException(
                -1, context.getString(R.string.error_message_address_is_not_found)
            )

            val geo = Geocoder(context, context.getSystemLocale())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geo.getFromLocation(latLng.latitude, latLng.longitude, 1) { listAddress ->
                    if (listAddress.isEmpty()) {
                        cancellableContinuation.resumeWithException(error)
                    } else {
                        cancellableContinuation.resume(listAddress.first())
                    }
                    cancellableContinuation.cancel()
                }
            } else {
                val listAddress = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (listAddress.isNullOrEmpty()) {
                    cancellableContinuation.resumeWithException(error)
                } else {
                    cancellableContinuation.resume(listAddress.first())
                }
                cancellableContinuation.cancel()
            }
        })
    }

    override fun getAddress(text: String): Flow<List<AutocompletePrediction>> = flow {
        val error = WeatherException.SnackBarException(
            -1, context.getString(R.string.error_message_address_is_not_found)
        )

        emit(suspendCancellableCoroutine { cancellableContinuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setTypesFilter(listOf(TypeFilter.ADDRESS.toString()))
                .setSessionToken(token)
                .setQuery(text)
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener {
                cancellableContinuation.resume(it.autocompletePredictions)
            }.addOnFailureListener {
                Timber.e(it)
                cancellableContinuation.resumeWithException(error)
            }.addOnCompleteListener {
                cancellableContinuation.cancel()
            }.addOnCanceledListener {
                cancellableContinuation.cancel()
            }
        })
    }
}
