package com.minhdtm.example.weapose.data.remote.interceptor

import android.os.Build
import com.minhdtm.example.weapose.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("OS", "Android-${Build.VERSION.SDK_INT}")
            .addHeader("Version", BuildConfig.VERSION_NAME)
            .build()

        return chain.proceed(request)
    }
}
