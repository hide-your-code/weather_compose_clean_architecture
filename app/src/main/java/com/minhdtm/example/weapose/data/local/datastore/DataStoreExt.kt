package com.minhdtm.example.weapose.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.minhdtm.example.weapose.data.exception.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

fun <T> DataStore<Preferences>.getValue(transform: (preferences: Preferences) -> T): Flow<T> = data.catch { exception ->
    if (exception is IOException) {
        emit(emptyPreferences())
    } else {
        throw ResponseException.preferences(exception)
    }
}.map {
    transform.invoke(it)
}

suspend fun DataStore<Preferences>.setValue(transform: (preference: MutablePreferences) -> Unit) = try {
    edit {
        transform.invoke(it)
    }
} catch (exception: Exception) {
    throw ResponseException.preferences(exception)
}
