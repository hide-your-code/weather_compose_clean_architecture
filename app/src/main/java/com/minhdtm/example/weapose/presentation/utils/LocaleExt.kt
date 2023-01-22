package com.minhdtm.example.weapose.presentation.utils

import android.content.Context
import android.os.Build
import java.util.Locale

@Suppress("DEPRECATION")
fun Context.getSystemLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    this.resources.configuration.locales[0]
} else {
    this.resources.configuration.locale
}
