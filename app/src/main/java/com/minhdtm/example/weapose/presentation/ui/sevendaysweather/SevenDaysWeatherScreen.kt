package com.minhdtm.example.weapose.presentation.ui.sevendaysweather

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.model.DayWeatherViewData
import com.minhdtm.example.weapose.presentation.model.factory.previewDayWeatherViewData
import com.minhdtm.example.weapose.presentation.theme.WeaposeTheme
import com.minhdtm.example.weapose.presentation.ui.Screen
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.ui.home.CurrentWeatherAppBar
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.toUVIndexAttention
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SevenDaysWeather(
    appState: WeatherAppState,
    viewModel: SevenDaysWeatherViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefresh,
        onRefresh = {
            viewModel.onRefresh()
        }
    )

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
        appState.getDataFromNextScreen(Constants.Key.LAT_LNG, Constants.Default.LAT_LNG_DEFAULT)?.collect { latLng ->
            if (latLng != Constants.Default.LAT_LNG_DEFAULT) {
                viewModel.getWeatherByLocation(latLng)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.LAT_LNG)
            }
        }
    }

    // Locale change
    LaunchedEffect(true) {
        appState.localChange.collectLatest {
            viewModel.onRefresh(false)
        }
    }

    // Get event
    LaunchedEffect(state) {
        val navigateToSearchByText = state.navigateToSearchByText

        when {
            navigateToSearchByText != null -> {
                appState.navigateToSearchByText(Screen.SevenDaysWeather, navigateToSearchByText)
            }

            else -> return@LaunchedEffect
        }

        viewModel.cleanEvent()
    }

    SevenDaysWeatherScreen(
        state = state,
        snackbarHostState = appState.snackbarHost,
        pullRefreshState = pullRefreshState,
        onDrawer = {
            appState.openDrawer()
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissDialog = {
            viewModel.hideError()
        },
        onNavigateSearch = {
            viewModel.onNavigateToSearch()
        },
        onClickExpandedItem = {
            viewModel.onClickExpandedItem(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SevenDaysWeatherScreen(
    state: SevenDaysViewState,
    snackbarHostState: SnackbarHostState,
    pullRefreshState: PullRefreshState,
    onShowSnackbar: (message: String) -> Unit = {},
    onDrawer: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
    onClickExpandedItem: (item: DayWeatherViewData) -> Unit = {},
) {
    WeatherScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = snackbarHostState,
        topBar = {
            CurrentWeatherAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(id = R.string.seven_days_in),
                city = state.address.ifBlank { stringResource(id = R.string.unknown_address) },
                onDrawer = onDrawer,
                onNavigateSearch = onNavigateSearch,
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = onDismissDialog,
    ) { _, viewState ->
        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            ListWeatherDay(
                modifier = Modifier.fillMaxSize(),
                list = viewState.listSevenDays,
                onClickExpandedItem = onClickExpandedItem,
            )

            PullRefreshIndicator(
                refreshing = state.isRefresh,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
fun ListWeatherDay(
    modifier: Modifier = Modifier,
    list: List<DayWeatherViewData> = emptyList(),
    onClickExpandedItem: (item: DayWeatherViewData) -> Unit = {},
) {
    val paddingBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = paddingBottom + 10.dp),
    ) {
        items(
            items = list,
            key = { item ->
                item.dateTime
            },
        ) { item ->
            WeatherDayItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClickExpandedItem = onClickExpandedItem,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeatherDayItem(
    modifier: Modifier = Modifier,
    item: DayWeatherViewData,
    onClickExpandedItem: (item: DayWeatherViewData) -> Unit = {},
) {
    val transition = updateTransition(targetState = item.isExpanded, label = "")

    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .clickable {
                    onClickExpandedItem.invoke(item)
                }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = item.dateTime,
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
                )

                Text(
                    text = item.weatherDetail,
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 10.dp),
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                )

                Column {
                    Text(
                        text = stringResource(id = R.string.degrees_c, item.maxTemp.toString()),
                        modifier = Modifier.padding(bottom = 5.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
                    )

                    Text(
                        text = stringResource(id = R.string.degrees_c, item.minTemp.toString()),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
                    )
                }

                transition.AnimatedContent(transitionSpec = {
                    if (!targetState) {
                        slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() with slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }) { state ->
                    Icon(
                        imageVector = if (!state) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }

        transition.AnimatedVisibility(
            visible = { it },
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.wind),
                    description = stringResource(id = R.string.kilometer_per_hour, item.windSpeed),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.humidity),
                    description = stringResource(id = R.string.home_text_humidity, item.humidity),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.uv_index),
                    description = stringResource(id = item.uvIndex.toUVIndexAttention(), item.uvIndex.toString()),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.sunset_sunrise),
                    description = stringResource(id = R.string.sunrise_sunset, item.sunrise, item.sunset),
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.inversePrimary)
    }
}

@Composable
fun WeatherInformation(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
) {
    Row(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
        )

        Text(
            text = description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light", showBackground = true)
@Composable
fun WeatherDayItemPreview() {
    WeaposeTheme {
        WeatherDayItem(
            item = previewDayWeatherViewData(),
            onClickExpandedItem = {}
        )
    }
}
