package com.minhdtm.example.weapose.domain.repositories

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<LatLng>

    fun getCurrentAddress(): Flow<Address>

    fun getAddressFromLocation(latLng: LatLng): Flow<Address>
}
