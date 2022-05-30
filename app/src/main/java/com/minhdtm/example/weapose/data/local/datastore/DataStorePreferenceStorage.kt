package com.minhdtm.example.weapose.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStorePreferenceStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceStorage {
    override suspend fun setDarkModeGoogleMap(isDarkMode: Boolean) {
        dataStore.setValue {
            it[PreferenceKeys.IS_DARK_MODE_GOOGLE_MAP] = isDarkMode
        }
    }

    override val isDarkModeGoogleMap: Flow<Boolean> = dataStore.getValue {
        it[PreferenceKeys.IS_DARK_MODE_GOOGLE_MAP] ?: false
    }

    object PreferenceKeys {
        val IS_DARK_MODE_GOOGLE_MAP = booleanPreferencesKey("IS_DARK_MODE_GOOGLE_MAP")
    }

    companion object {
        const val PREFS_NAME = "my_movie_datastore"
    }
}
