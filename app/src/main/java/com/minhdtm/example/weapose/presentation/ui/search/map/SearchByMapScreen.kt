package com.minhdtm.example.weapose.presentation.ui.search.map

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.theme.WeaposeTheme
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.utils.Constants

@Composable
fun SearchByMap(
    appState: WeatherAppState,
    viewModel: SearchByMapViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState()

    var mapStyleOptions by remember {
        mutableStateOf(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_google_map_night))
    }

    LaunchedEffect(state.isDarkMode) {
        mapStyleOptions = if (state.isDarkMode) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.style_google_map_night)
        } else {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.style_google_map_light)
        }
    }

    LaunchedEffect(state) {
        when {
            state.popupToRoute != null -> {
                state.address?.let { address ->
                    val params = mutableMapOf<String, Any>()
                    params[Constants.Key.LAT_LNG] = LatLng(address.latitude, address.longitude)
                    appState.popBackStack(popToRoute = state.popupToRoute, params = params)
                }
            }

            state.moveCamera != null -> {
                state.moveCamera?.let {
                    cameraPositionState.move(CameraUpdateFactory.newLatLng(it))
                }
            }

            else -> return@LaunchedEffect
        }
        viewModel.cleanEvent()
    }

    SearchByMapScreen(
        state = state,
        cameraPositionState = cameraPositionState,
        mapProperties = mapStyleOptions,
        onShowSnackbar = { message ->
            appState.showSnackbar(message)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        },
        onBack = {
            appState.popBackStack()
        },
        onChangeModeGoogleMap = {
            viewModel.setDarkMode()
        },
        onMapClick = { latLng ->
            viewModel.setMarker(latLng)
        },
        onClickDone = {
            viewModel.onClickDone()
        },
        onClickCurrentLocation = {
            viewModel.onClickCurrentLocation()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByMapScreen(
    state: SearchByMapViewState,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    mapProperties: MapStyleOptions? = null,
    onBack: () -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    onShowSnackbar: (String) -> Unit = {},
    onChangeModeGoogleMap: () -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
    onClickDone: () -> Unit = {},
    onClickCurrentLocation: () -> Unit = {},
) {
    WeaposeTheme(darkTheme = state.isDarkMode) {
        WeatherScaffold(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onDismissErrorDialog = onDismissErrorDialog,
            onShowSnackbar = onShowSnackbar,
        ) { _, viewState ->
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = MapProperties(mapStyleOptions = mapProperties),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                    onMapClick = onMapClick,
                    cameraPositionState = cameraPositionState,
                ) {
                    if (viewState.marker != null) {
                        Marker(state = viewState.marker)
                    }
                }

                SearchAppBar(
                    isDarkMode = viewState.isDarkMode,
                    onBack = onBack,
                    onChangeModeGoogleMap = onChangeModeGoogleMap,
                )

                ResultMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp, end = 20.dp, start = 20.dp),
                    address = viewState.address?.getAddressLine(0),
                    onClickDone = onClickDone,
                    onClickCurrentLocation = onClickCurrentLocation,
                )
            }
        }
    }
}

@Composable
fun ResultMap(
    modifier: Modifier = Modifier,
    address: String? = null,
    onClickDone: () -> Unit = {},
    onClickCurrentLocation: () -> Unit = {},
) {
    Column(modifier = modifier) {
        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .align(Alignment.End),
            onClick = onClickCurrentLocation,

            ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
            )
        }

        Surface(
            modifier = Modifier.wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.address),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Text(
                    text = address ?: stringResource(id = R.string.unknown_address),
                    style = MaterialTheme.typography.labelMedium,
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = onClickDone,
                    enabled = address != null,
                ) {
                    Icon(
                        imageVector = if (address != null) Icons.Sharp.Done else Icons.Sharp.Close,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchAppBar(
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onChangeModeGoogleMap: () -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        title = {

        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            Button(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .height(40.dp),
                onClick = onChangeModeGoogleMap,
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = isDarkMode,
                    transitionSpec = {
                        if (!targetState) { // Dark mode
                            slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()
                        } else { // Light mode
                            slideInVertically { height -> -height } + fadeIn() with slideOutVertically { height -> height } + fadeOut()
                        }.using(
                            SizeTransform(clip = false)
                        )
                    },
                ) {
                    val icon = rememberSaveable(isDarkMode) {
                        if (isDarkMode) {
                            R.drawable.ic_dark_mode
                        } else {
                            R.drawable.ic_light_mode
                        }
                    }

                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                    )
                }
            }
        })
}

@Preview
@Composable
fun SearchAppBarPreview_Light() {
    MaterialTheme {
        SearchAppBar(isDarkMode = false)
    }
}

@Preview
@Composable
fun SearchAppBarPreview_Dark() {
    MaterialTheme {
        SearchAppBar(isDarkMode = true)
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ResultPreview() {
    MaterialTheme {
        ResultMap(modifier = Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
fun SearchByMapPreview() {
    MaterialTheme {
        SearchByMapScreen(
            state = SearchByMapViewState()
        )
    }
}
