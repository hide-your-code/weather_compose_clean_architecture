package com.minhdtm.example.weapose.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.presentation.base.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun <VS : ViewState> WeatherScaffold(
    modifier: Modifier = Modifier,
    state: VS,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    onShowSnackbar: (message: String) -> Unit = {},
    onErrorPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onErrorNegativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onDismissErrorDialog: () -> Unit,
    content: @Composable (viewState: VS) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { paddingValues ->
        WeatherHandleError(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = state,
            onShowSnackBar = onShowSnackbar,
            onPositiveAction = onErrorPositiveAction,
            onNegativeAction = onErrorNegativeAction,
            onDismissErrorDialog = onDismissErrorDialog,
        ) { viewState ->
            content.invoke(viewState)
        }
    }
}
