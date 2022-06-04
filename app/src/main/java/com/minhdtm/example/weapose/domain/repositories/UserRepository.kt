package com.minhdtm.example.weapose.domain.repositories

import com.minhdtm.example.weapose.data.local.room.HistorySearchAddressEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun setDarkModeGoogleMap(isDarkMode: Boolean)

    val isDarkModeGoogleMap: Flow<Boolean>

    fun getSearchAddress(): Flow<List<HistorySearchAddressEntity>>

    suspend fun addSearchAddress(historySearchAddressEntity: HistorySearchAddressEntity)

    suspend fun clearAllSearchAddress()
}
