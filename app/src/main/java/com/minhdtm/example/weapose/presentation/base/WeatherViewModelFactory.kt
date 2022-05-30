package com.minhdtm.example.weapose.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry

class WeatherViewModelFactory(
    private val currentBackStackEntry: NavBackStackEntry,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        modelClass.getConstructor(NavBackStackEntry::class.java).newInstance(currentBackStackEntry)
}
