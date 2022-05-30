package com.minhdtm.example.weapose.presentation.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState

@Composable
fun Settings(
    appState: WeatherAppState,
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        snackbarHostState = appState.snackbarHost,
        onDrawer = {
            appState.openDrawer()
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissDialog = {
            viewModel.hideError()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingViewState,
    snackbarHostState: SnackbarHostState,
    onShowSnackbar: (message: String) -> Unit = {},
    onDrawer: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
) {
    WeatherScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = snackbarHostState,
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(text = "Setting screen")
                },
                navigationIcon = {
                    IconButton(onClick = onDrawer) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "",
                        )
                    }
                },
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = onDismissDialog,
    ) { _ ->

    }
}
