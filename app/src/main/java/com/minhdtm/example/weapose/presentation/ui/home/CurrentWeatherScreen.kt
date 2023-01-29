package com.minhdtm.example.weapose.presentation.ui.home

import android.Manifest
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.NEXUS_5
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData
import com.minhdtm.example.weapose.presentation.model.factory.previewCurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.theme.WeaposeTheme
import com.minhdtm.example.weapose.presentation.ui.Screen
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun CurrentWeather(
    appState: WeatherAppState,
    viewModel: CurrentWeatherViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefresh,
        onRefresh = {
            viewModel.onRefreshCurrentWeather()
        },
    )

    val context = LocalContext.current

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        when {
            permissions.all { it.value } -> viewModel.getCurrentLocation()
            else -> viewModel.permissionIsNotGranted()
        }
    }

    // Get data from back
    LaunchedEffect(true) {
        appState.getDataFromNextScreen(Constants.Key.ADDRESS_NAME, "")?.collect {
            if (it.isNotBlank()) {
                viewModel.getWeatherByAddressName(addressName = it)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.ADDRESS_NAME)
            }
        }
    }

    LaunchedEffect(true) {
        appState.getDataFromNextScreen(Constants.Key.LAT_LNG, Constants.Default.LAT_LNG_DEFAULT)?.collect {
            if (it != LatLng(0.0, 0.0)) {
                viewModel.getWeatherByLocation(it)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.LAT_LNG)
            }
        }
    }

    // Locale change
    LaunchedEffect(true) {
        appState.localChange.collectLatest {
            viewModel.onRefreshCurrentWeather(false)
        }
    }

    // Get event
    LaunchedEffect(state) {
        val navigateToSearch = state.navigateSearch
        val requestPermission = state.isRequestPermission

        when {
            requestPermission -> {
                when {
                    locationPermissionState.allPermissionsGranted -> {
                        viewModel.getCurrentLocation()
                    }

                    locationPermissionState.shouldShowRationale -> {
                        viewModel.permissionIsNotGranted()
                    }

                    else -> {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }
                }
            }

            navigateToSearch != null -> {
                appState.navigateToSearchByText(Screen.CurrentWeather, navigateToSearch)
            }

            else -> return@LaunchedEffect
        }
        viewModel.cleanEvent()
    }

    CurrentWeatherScreen(
        state = state,
        pullRefreshState = pullRefreshState,
        snackbarHostState = appState.snackbarHost,
        onDrawer = {
            appState.openDrawer()
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        },
        onNavigateSearch = {
            viewModel.navigateToSearchByMap()
        },
        onErrorPositiveAction = { action, _ ->
            action?.let {
                when (it) {
                    ActionType.RETRY_API -> {
                        viewModel.retry()
                    }

                    ActionType.OPEN_PERMISSION -> {
                        appState.openAppSetting(context)
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CurrentWeatherScreen(
    state: CurrentWeatherViewState,
    pullRefreshState: PullRefreshState,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onDrawer: () -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
    onShowSnackbar: (message: String) -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onErrorPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
) {
    WeatherScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = snackbarHostState,
        onDismissErrorDialog = onDismissErrorDialog,
        onShowSnackbar = onShowSnackbar,
        onErrorPositiveAction = onErrorPositiveAction,
        topBar = {
            CurrentWeatherAppBar(
                modifier = Modifier.statusBarsPadding(),
                city = state.currentWeather?.city,
                onDrawer = onDrawer,
                onNavigateSearch = onNavigateSearch,
            )
        },
    ) { _, viewState ->
        Box(Modifier.pullRefresh(pullRefreshState)) {
            viewState.currentWeather?.let {
                HomeContent(
                    currentWeather = it,
                    listHourly = viewState.listHourlyWeatherToday,
                )
            }

            PullRefreshIndicator(
                refreshing = state.isRefresh, state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun HomeContent(
    currentWeather: CurrentWeatherViewData,
    listHourly: List<HourWeatherViewData>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NowWeather(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            currentWeather = currentWeather,
        )

        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(
                    top = 50.dp,
                    bottom = 30.dp,
                )
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(
                    count = listHourly.size,
                    key = {
                        listHourly[it].timeStamp
                    },
                ) { index ->
                    WeatherHour(
                        hour = listHourly[index].time,
                        weather = listHourly[index].weatherIcon,
                    )
                }
            }
        }
    }
}

@Composable
fun NowWeather(
    modifier: Modifier,
    currentWeather: CurrentWeatherViewData,
) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(id = currentWeather.background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 50.dp)
                .weight(1f),
            alignment = Alignment.CenterEnd,
            contentScale = ContentScale.FillHeight,
        )

        Column(
            modifier = Modifier
                .padding(end = 15.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    id = R.string.home_text_celsius_high_low,
                    currentWeather.maxTemp,
                    currentWeather.minTemp,
                ),
            )

            Degrees(
                currentWeather = currentWeather.weather,
                currentTemp = currentWeather.temp,
            )

            DetailWeather(
                iconId = R.drawable.ic_sun_rise,
                title = stringResource(id = R.string.sun_rise),
                description = currentWeather.sunRise,
            )

            DetailWeather(
                iconId = R.drawable.ic_wind,
                title = stringResource(id = R.string.wind),
                description = stringResource(id = R.string.home_text_meter_per_second, currentWeather.wind),
            )

            DetailWeather(
                iconId = R.drawable.ic_humidity,
                title = stringResource(id = R.string.humidity),
                description = stringResource(id = R.string.home_text_humidity, currentWeather.humidity),
            )
        }
    }
}

@Composable
fun DetailWeather(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    title: String,
    description: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = iconId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 5.dp),
        )

        Text(
            text = description,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    city: String? = null,
    onDrawer: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            if (title.isNotBlank()) {
                Text(text = title, maxLines = 1, overflow = TextOverflow.Visible)
            }
        },
        navigationIcon = {
            IconButton(onClick = onDrawer) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = null,
                )
            }
        },
        actions = {
            Card(
                onClick = onNavigateSearch,
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Sharp.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp),
                    )

                    Text(
                        text = city ?: stringResource(id = R.string.unknown_address),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
    )
}

@Composable
fun Degrees(
    modifier: Modifier = Modifier,
    currentTemp: String,
    currentWeather: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currentTemp,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.alignBy(LastBaseline),
            )

            Column(modifier = Modifier.alignBy(LastBaseline)) {
                Text(text = "o", modifier = Modifier.padding(bottom = 10.dp))

                Text(text = "c")
            }
        }

        Text(
            text = currentWeather,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 22.sp),
        )
    }
}

@Composable
fun WeatherHour(
    modifier: Modifier = Modifier,
    hour: String,
    @DrawableRes weather: Int,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = hour,
            style = MaterialTheme.typography.titleMedium,
        )

        Image(
            painter = painterResource(id = weather),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(top = 15.dp),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NowWeatherPreview() {
    WeaposeTheme {
        NowWeather(
            modifier = Modifier.size(500.dp),
            currentWeather = previewCurrentWeatherViewData(),
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DegreesPreview() {
    WeaposeTheme {
        Degrees(
            currentWeather = previewCurrentWeatherViewData().weather,
            currentTemp = previewCurrentWeatherViewData().temp,
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppBarPreview() {
    WeaposeTheme {
        CurrentWeatherAppBar(city = previewCurrentWeatherViewData().city)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(name = "Light", showBackground = true, device = NEXUS_5)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, device = PIXEL_4_XL)
@Composable
private fun ScreenPreview() {
    WeaposeTheme {
        CurrentWeatherScreen(
            state = CurrentWeatherViewState(),
            pullRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {})
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeatherHourPreview() {
    WeaposeTheme {
        WeatherHour(
            hour = "10:00",
            weather = R.drawable.ic_clear_sky,
        )
    }
}
