package com.minhdtm.example.weapose.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.component.NavigationDrawerLabel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeatherApp(appState: WeatherAppState = rememberWeatherAppState()) {
    val systemUiController = rememberSystemUiController()
    val darkIcons = isSystemInDarkTheme()

    SideEffect {
        if (!appState.isCustomDarkMode) {
            systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !darkIcons)
        }
    }

    ModalNavigationDrawer(
        drawerState = appState.drawer,
        gesturesEnabled = appState.shouldEnableGesture,
        drawerContent = {
            WeatherDrawerContent(
                selectedItem = appState.drawerItemSelected,
                onClickHome = {
                    appState.navigateToHome()
                },
                onClickSettings = {
                    appState.navigateToSettings()
                },
            )
        },
    ) {
        Scaffold(
            containerColor = Color.Transparent,
        ) {
            AnimatedNavHost(
                navController = appState.controller,
                startDestination = NestedGraph.HOME.route,
            ) {
                home(appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDrawerContent(
    selectedItem: DrawerTab,
    onClickHome: () -> Unit,
    onClickSettings: () -> Unit,
) {
    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

    NavigationDrawerLabel {
        Text(text = stringResource(id = R.string.app_name))
    }

    NavigationDrawerItem(
        icon = {
            Icon(DrawerTab.HOME.icon, contentDescription = null)
        },
        label = {
            Text(stringResource(id = DrawerTab.HOME.title))
        },
        selected = DrawerTab.HOME == selectedItem,
        onClick = {
            onClickHome.invoke()
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )

    NavigationDrawerItem(
        icon = {
            Icon(DrawerTab.SETTINGS.icon, contentDescription = null)
        },
        label = {
            Text(stringResource(id = DrawerTab.SETTINGS.title))
        },
        selected = DrawerTab.SETTINGS == selectedItem,
        onClick = {
            onClickSettings.invoke()
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}
