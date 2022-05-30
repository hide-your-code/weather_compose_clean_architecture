package com.minhdtm.example.weapose.presentation.utils

import com.google.android.gms.maps.model.LatLng

object Constants {
    object DateFormat {
        const val HH_mm = "HH:mm"
    }

    object Key {
        const val LAT_LNG = "lat_lng"
        const val LAT = "lat"
        const val LNG = "lng"
    }

    object Default {
        val LAT_LNG_DEFAULT = LatLng(0.0, 0.0)
    }
}
