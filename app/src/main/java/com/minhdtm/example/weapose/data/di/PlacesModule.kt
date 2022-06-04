package com.minhdtm.example.weapose.data.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    @Provides
    fun providePlaceClient(@ApplicationContext context: Context): PlacesClient = Places.createClient(context)

    @Provides
    fun provideAutocompleteSessionToken(): AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
}
