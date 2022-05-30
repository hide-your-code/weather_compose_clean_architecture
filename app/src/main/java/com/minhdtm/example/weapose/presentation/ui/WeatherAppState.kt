package com.minhdtm.example.weapose.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.ui.home.Home
import com.minhdtm.example.weapose.presentation.ui.search.bymap.SearchByMap
import com.minhdtm.example.weapose.presentation.ui.settings.Settings
import com.minhdtm.example.weapose.presentation.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Settings : Screen("settings")

    object Search : Screen("search")
}

enum class NestedGraph(val route: String) {
    HOME(route = "home_nav"),
}

enum class DrawerTab(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    HOME(
        route = Screen.Home.route,
        title = R.string.home_tab,
        icon = Icons.Default.Home,
    ),
    SETTINGS(
        route = Screen.Settings.route,
        title = R.string.settings_tab,
        icon = Icons.Default.Settings,
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(appState: WeatherAppState) {
    navigation(
        route = NestedGraph.HOME.route,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Home.route) {
            Home(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(route = Screen.Settings.route) {
            Settings(
                appState = appState,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = "${Screen.Search.route}?lat={${Constants.Key.LAT}}&lng={${Constants.Key.LNG}}",
            arguments = listOf(
                navArgument(Constants.Key.LAT) { type = NavType.StringType },
                navArgument(Constants.Key.LNG) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            SearchByMap(
                appState = appState,
                viewModel = hiltViewModel(backStackEntry),
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
            return drawerTabs.firstOrNull { it.route == route } ?: DrawerTab.HOME
        }

    val isCustomDarkMode: Boolean
        get() {
            val listScreenCustomizeDarkMode = listOf(Screen.Search.route)
            val route = controller.currentBackStackEntry?.destination?.route
            return route in listScreenCustomizeDarkMode
        }

    fun <T> getDataFromNextScreen(key: String, defaultValue: T): StateFlow<T>? =
        controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(key, defaultValue)

    fun <T> removeDataFromNextScreen(key: String) {
        controller.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
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

    fun popBackStack(params: Map<String, Any>? = null) {
        params?.forEach { data ->
            controller.previousBackStackEntry?.savedStateHandle?.set(data.key, data.value)
        }

        controller.popBackStack()
    }

    fun navigateToHome() {
        closeDrawer()

        controller.navigate(route = Screen.Home.route) {
            popUpTo(Screen.Home.route) {
                inclusive = true
            }
        }
    }

    fun navigateToSettings() {
        closeDrawer()

        controller.navigate(route = Screen.Settings.route)
    }

    fun navigateToSearch(latLng: LatLng) {
        controller.navigate(
            route = "${Screen.Search.route}?lat=${latLng.latitude}&lng=${latLng.longitude}"
        )
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
