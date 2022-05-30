package com.minhdtm.example.weapose.domain.usecase

import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.domain.repositories.LocationRepository
import com.minhdtm.example.weapose.domain.usecase.base.FlowUseCase
import com.minhdtm.example.weapose.presentation.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentLocationUseCase @Inject constructor(
    @MainDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val locationRepository: LocationRepository,
) : FlowUseCase<Unit, LatLng>(coroutineDispatcher) {
    override fun execute(params: Unit?): Flow<LatLng> = locationRepository.getCurrentLocation()
}
