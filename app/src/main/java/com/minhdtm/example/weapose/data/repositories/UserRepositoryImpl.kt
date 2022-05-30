package com.minhdtm.example.weapose.data.repositories

import com.minhdtm.example.weapose.data.local.datastore.PreferenceStorage
import com.minhdtm.example.weapose.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
) : UserRepository {
    override suspend fun setDarkModeGoogleMap(isDarkMode: Boolean) = preferenceStorage.setDarkModeGoogleMap(isDarkMode)

    override val isDarkModeGoogleMap: Flow<Boolean> = preferenceStorage.isDarkModeGoogleMap
}
