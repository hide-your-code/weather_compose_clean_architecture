package com.minhdtm.example.weapose.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.ui.home.CurrentWeather
import com.minhdtm.example.weapose.presentation.ui.infomation.Information
import com.minhdtm.example.weapose.presentation.ui.search.map.SearchByMap
import com.minhdtm.example.weapose.presentation.ui.search.text.SearchByText
import com.minhdtm.example.weapose.presentation.ui.settings.Settings
import com.minhdtm.example.weapose.presentation.ui.sevendaysweather.SevenDaysWeather
import com.minhdtm.example.weapose.presentation.ui.splash.Splash
import com.minhdtm.example.weapose.presentation.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")

    object CurrentWeather : Screen("current_weather")

    object SevenDaysWeather : Screen("seven_days_weather")

    object Settings : Screen("settings")

    object Info : Screen("info")

    object SearchByMap : Screen("search_by_map")

    object SearchByText : Screen("search_by_text")
}

enum class NestedGraph(val route: String) {
    HOME(route = "home_nav"), SPLASH(route = "splash_nav"),
}

enum class DrawerTab(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
) {
    CURRENT_WEATHER(
        route = Screen.CurrentWeather.route,
        title = R.string.current_weather_tab,
        icon = R.drawable.ic_cloud,
    ),
    SEVEN_DAYS_WEATHER(
        route = Screen.SevenDaysWeather.route,
        title = R.string.seven_days_weather_tab,
        icon = R.drawable.ic_calendar,
    ),
    SETTINGS(
        route = Screen.Settings.route,
        title = R.string.settings_tab,
        icon = R.drawable.ic_settings,
    ),
    INFO(
        route = Screen.Info.route,
        title = R.string.information_tab,
        icon = R.drawable.ic_info,
    ),
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.splash(appState: WeatherAppState) {
    navigation(
        route = NestedGraph.SPLASH.route,
        startDestination = Screen.Splash.route,
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            Splash(appState)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(appState: WeatherAppState) {
    navigation(
        route = NestedGraph.HOME.route,
        startDestination = Screen.CurrentWeather.route,
    ) {
        composable(
            route = Screen.CurrentWeather.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            CurrentWeather(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            Settings(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Screen.SevenDaysWeather.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            SevenDaysWeather(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Screen.Info.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            Information(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = "${Screen.SearchByMap.route}?lat={${Constants.Key.LAT}}&lng={${Constants.Key.LNG}}&from_route={${Constants.Key.FROM_ROUTE}}",
            arguments = listOf(
                navArgument(Constants.Key.LAT) { type = NavType.StringType },
                navArgument(Constants.Key.LNG) { type = NavType.StringType },
                navArgument(Constants.Key.FROM_ROUTE) { type = NavType.StringType },
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            SearchByMap(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = "${Screen.SearchByText.route}?lat={${Constants.Key.LAT}}&lng={${Constants.Key.LNG}}&from_route={${Constants.Key.FROM_ROUTE}}",
            arguments = listOf(
                navArgument(Constants.Key.LAT) { type = NavType.StringType },
                navArgument(Constants.Key.LNG) { type = NavType.StringType },
                navArgument(Constants.Key.FROM_ROUTE) { type = NavType.StringType },
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            },
        ) {
            SearchByText(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun rememberWeatherAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    controller: NavHostController = rememberAnimatedNavController(),
    drawer: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
): WeatherAppState = remember(coroutineScope, controller, drawer, snackbarHost) {
    WeatherAppState(coroutineScope, controller, drawer, snackbarHost)
}

@OptIn(ExperimentalMaterial3Api::class)
class WeatherAppState(
    private val coroutineScope: CoroutineScope,
    val controller: NavHostController,
    val drawer: DrawerState,
    val snackbarHost: SnackbarHostState,
) {
    private val drawerTabs = DrawerTab.values()

    val shouldEnableGesture: Boolean
        @Composable get() = controller.currentBackStackEntryAsState().value?.destination?.route in drawerTabs.map { it.route }

    val drawerItemSelected: DrawerTab
        @Composable get() {
            val route = controller.currentBackStackEntryAsState().value?.destination?.route
            return drawerTabs.firstOrNull { it.route == route } ?: DrawerTab.CURRENT_WEATHER
        }

    val isCustomDarkMode: Boolean
        get() {
            val listScreenCustomizeDarkMode = listOf(Screen.SearchByMap.route)
            val route = controller.currentBackStackEntry?.destination?.route
            return route in listScreenCustomizeDarkMode
        }

    private val _localeChange = Channel<Locale>()
    val localChange: Flow<Locale> = _localeChange.receiveAsFlow()

    fun currentDestinationIs(route: String): Boolean = controller.currentBackStackEntry?.destination?.route == route

    fun <T> getDataFromNextScreen(key: String, defaultValue: T): StateFlow<T>? =
        controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(key, defaultValue)

    fun <T> removeDataFromNextScreen(key: String) {
        controller.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }

    fun onLocaleChange(locale: Locale) {
        coroutineScope.launch {
            _localeChange.send(locale)
        }
    }

    fun openDrawer() {
        coroutineScope.launch {
            drawer.open()
        }
    }

    fun closeDrawer() {
        coroutineScope.launch {
            drawer.close()
        }
    }

    fun showSnackbar(message: String) {
        coroutineScope.launch {
            snackbarHost.showSnackbar(message)
        }
    }

    fun popBackStack(popToRoute: String? = null, params: Map<String, Any>? = null) {
        if (popToRoute == null) {
            params?.forEach { data ->
                controller.previousBackStackEntry?.savedStateHandle?.set(data.key, data.value)
            }
            controller.popBackStack()
        } else {
            params?.forEach { data ->
                controller.getBackStackEntry(popToRoute).savedStateHandle[data.key] = data.value
            }
            controller.popBackStack(route = popToRoute, inclusive = false)
        }
    }

    fun navigateToHome() {
        closeDrawer()
        controller.navigate(route = Screen.CurrentWeather.route) {
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }

    fun navigateToCurrentWeather() {
        closeDrawer()

        controller.navigate(route = Screen.CurrentWeather.route) {
            popUpTo(Screen.CurrentWeather.route) {
                inclusive = true
            }
        }
    }

    fun navigateToSevenDaysWeather() {
        closeDrawer()

        controller.navigate(route = Screen.SevenDaysWeather.route)
    }

    fun navigateToSettings() {
        closeDrawer()

        controller.navigate(route = Screen.Settings.route)
    }

    fun navigateToInformation() {
        closeDrawer()

        controller.navigate(route = Screen.Info.route)
    }

    fun navigateToSearchByMap(fromRoute: String, latLng: LatLng) {
        controller.navigate(route = "${Screen.SearchByMap.route}?lat=${latLng.latitude}&lng=${latLng.longitude}&from_route=${fromRoute}")
    }

    fun navigateToSearchByText(fromRoute: Screen, latLng: LatLng) {
        controller.navigate(route = "${Screen.SearchByText.route}?lat=${latLng.latitude}&lng=${latLng.longitude}&from_route=${fromRoute.route}")
    }

    fun openAppSetting(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
