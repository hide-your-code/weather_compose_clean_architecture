package com.minhdtm.example.weapose.domain.repositories

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun setDarkModeGoogleMap(isDarkMode: Boolean)

    val isDarkModeGoogleMap: Flow<Boolean>
}
