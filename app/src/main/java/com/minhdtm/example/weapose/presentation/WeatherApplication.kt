package com.minhdtm.example.weapose.presentation

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.libraries.places.api.Places
import com.minhdtm.example.weapose.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
            )
        } else {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }
        val value = appInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""

        Places.initialize(this, value)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
