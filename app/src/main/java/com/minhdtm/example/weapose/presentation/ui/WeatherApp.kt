package com.minhdtm.example.weapose.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.minhdtm.example.weapose.BuildConfig
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.broadcast.LocaleChangeBroadcast
import com.minhdtm.example.weapose.presentation.component.NavigationDrawerLabel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeatherApp(appState: WeatherAppState = rememberWeatherAppState()) {
    val systemUiController = rememberSystemUiController()
    val darkIcons = isSystemInDarkTheme()

    val localFocusManager = LocalFocusManager.current
    val localContext = LocalContext.current

    SideEffect {
        if (!appState.isCustomDarkMode) {
            systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !darkIcons)
        }
    }

    DisposableEffect(true) {
        val broadcast = LocaleChangeBroadcast(appState::onLocaleChange)

        localContext.registerReceiver(broadcast, IntentFilter(Intent.ACTION_LOCALE_CHANGED))

        onDispose {
            localContext.unregisterReceiver(broadcast)
        }
    }

    ModalNavigationDrawer(
        drawerState = appState.drawer,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                localFocusManager.clearFocus()
            }
        },
        gesturesEnabled = appState.shouldEnableGesture,
        drawerContent = {
            ModalDrawerSheet {
                WeatherDrawerContent(
                    selectedItem = appState.drawerItemSelected,
                    onClickCurrentWeather = {
                        if (!appState.currentDestinationIs(Screen.CurrentWeather.route)) {
                            appState.navigateToCurrentWeather()
                        } else {
                            appState.closeDrawer()
                        }
                    },
                    onClickSevenDaysWeather = {
                        if (!appState.currentDestinationIs(Screen.SevenDaysWeather.route)) {
                            appState.navigateToSevenDaysWeather()
                        } else {
                            appState.closeDrawer()
                        }
                    },
                    onClickSettings = {
                        if (!appState.currentDestinationIs(Screen.Settings.route)) {
                            appState.navigateToSettings()
                        } else {
                            appState.closeDrawer()
                        }
                    },
                    onClickInformation = {
                        if (!appState.currentDestinationIs(Screen.Info.route)) {
                            appState.navigateToInformation()
                        } else {
                            appState.closeDrawer()
                        }
                    },
                )
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            AnimatedNavHost(
                navController = appState.controller,
                startDestination = NestedGraph.SPLASH.route,
            ) {
                splash(appState)

                home(appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.WeatherDrawerContent(
    selectedItem: DrawerTab,
    onClickCurrentWeather: () -> Unit = {},
    onClickSevenDaysWeather: () -> Unit = {},
    onClickSettings: () -> Unit = {},
    onClickInformation: () -> Unit = {},
) {
    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = R.mipmap.ic_launcher_app_foreground),
            contentDescription = null,
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
        )
    }

    NavigationDrawerLabel {
        Text(text = stringResource(id = R.string.feature_group_tab))
    }

    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = DrawerTab.CURRENT_WEATHER.icon),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
            )
        },
        label = {
            Text(text = stringResource(id = R.string.current_weather_tab))
        },
        selected = DrawerTab.CURRENT_WEATHER == selectedItem,
        onClick = onClickCurrentWeather,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = DrawerTab.SEVEN_DAYS_WEATHER.icon),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
            )
        },
        label = {
            Text(text = stringResource(id = R.string.seven_days_weather_tab))
        },
        selected = DrawerTab.SEVEN_DAYS_WEATHER == selectedItem,
        onClick = onClickSevenDaysWeather,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )

    NavigationDrawerLabel {
        Text(text = stringResource(id = R.string.another_group_tab))
    }

    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = DrawerTab.SETTINGS.icon),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
            )
        },
        label = {
            Text(stringResource(id = DrawerTab.SETTINGS.title))
        },
        selected = DrawerTab.SETTINGS == selectedItem,
        onClick = onClickSettings,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = DrawerTab.INFO.icon),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
            )
        },
        label = {
            Text(text = stringResource(id = DrawerTab.INFO.title))
        },
        selected = DrawerTab.INFO == selectedItem,
        onClick = onClickInformation,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )

    Spacer(modifier = Modifier.weight(1f))

    NavigationDrawerLabel {
        Text(text = "Build version: ${BuildConfig.VERSION_NAME}")
    }

    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
}
