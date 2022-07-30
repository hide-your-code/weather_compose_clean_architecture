package com.minhdtm.example.weapose.presentation.ui.infomation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun Information(
    appState: WeatherAppState,
    viewModel: InformationViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    InformationScreen(
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
fun InformationScreen(
    state: InformationViewState,
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
                    Text(text = "Information screen")
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
    ) { _, _ ->

    }
}
