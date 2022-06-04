package com.minhdtm.example.weapose.presentation

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.minhdtm.example.weapose.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(this, BuildConfig.MAPS_API_KEY)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
