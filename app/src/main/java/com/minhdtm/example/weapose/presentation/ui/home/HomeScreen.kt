package com.minhdtm.example.weapose.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Devices.NEXUS_5
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData
import com.minhdtm.example.weapose.presentation.model.factory.previewCurrentWeatherViewData
import com.minhdtm.example.weapose.presentation.theme.MyMovieAppTheme
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.Constants.Default.LAT_LNG_DEFAULT
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(
    appState: WeatherAppState,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        when {
            permissions.all { it.value } -> viewModel.getLocation()
            else -> viewModel.permissionIsNotGranted()
        }
    }

    // Get data from back
    LaunchedEffect(true) {
        appState.getDataFromNextScreen(Constants.Key.LAT_LNG, LAT_LNG_DEFAULT)?.collect {
            if (it != LatLng(0.0, 0.0)) {
                viewModel.getLocation(it)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.LAT_LNG)
            }
        }
    }

    // Get event
    LaunchedEffect(true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.CheckPermission -> {
                    when {
                        locationPermissionState.allPermissionsGranted -> {
                            viewModel.getLocation()
                        }
                        locationPermissionState.shouldShowRationale -> {
                            viewModel.permissionIsNotGranted()
                        }
                        else -> {
                            locationPermissionState.launchMultiplePermissionRequest()
                        }
                    }
                }
                is HomeEvent.NavigateToSearchByMap -> {
                    appState.navigateToSearch(event.latLng)
                }
            }
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = appState.snackbarHost,
        onDrawer = {
            appState.openDrawer()
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onRefresh = {
            viewModel.onRefreshCurrentWeather()
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeViewState,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onRefresh: () -> Unit = {},
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
            HomeAppBar(
                modifier = Modifier.statusBarsPadding(),
                city = state.currentWeather?.city,
                onDrawer = onDrawer,
                onNavigateSearch = onNavigateSearch,
            )
        },
    ) { viewState ->
        SwipeRefresh(
            state = SwipeRefreshState(state.isRefresh),
            onRefresh = onRefresh,
        ) {
            if (viewState.currentWeather != null) {
                HomeContent(
                    currentWeather = viewState.currentWeather,
                    listHourly = viewState.listHourlyWeatherToday,
                )
            }
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
fun HomeAppBar(
    modifier: Modifier = Modifier,
    city: String? = null,
    onDrawer: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
) {
    SmallTopAppBar(
        modifier = modifier,
        title = {

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
    MyMovieAppTheme {
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
    MyMovieAppTheme {
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
    MyMovieAppTheme {
        HomeAppBar(city = previewCurrentWeatherViewData().city)
    }
}

@Preview(name = "Light", showBackground = true, device = NEXUS_5)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, device = PIXEL_4_XL)
@Composable
private fun ScreenPreview() {
    MyMovieAppTheme {
        HomeScreen(HomeViewState())
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeatherHourPreview() {
    MyMovieAppTheme {
        WeatherHour(
            hour = "10:00",
            weather = R.drawable.ic_clear_sky,
        )
    }
}
