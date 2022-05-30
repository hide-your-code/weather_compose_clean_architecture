package com.minhdtm.example.weapose.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    suspend fun setDarkModeGoogleMap(isDarkMode: Boolean)
    val isDarkModeGoogleMap: Flow<Boolean>
}
