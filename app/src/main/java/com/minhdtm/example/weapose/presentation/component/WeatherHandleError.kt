package com.minhdtm.example.weapose.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.domain.enums.ActionType
import com.minhdtm.example.weapose.domain.exception.WeatherException
import com.minhdtm.example.weapose.domain.model.AlertDialog
import com.minhdtm.example.weapose.presentation.base.ViewState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun <VS : ViewState> WeatherHandleError(
    modifier: Modifier = Modifier,
    state: VS,
    onShowSnackBar: (message: String) -> Unit = {},
    onPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onNegativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onDismissErrorDialog: () -> Unit = {},
    content: @Composable (VS) -> Unit,
) {
    Box(modifier = modifier) {
        content(state)

        if (state.isLoading) {
            FullScreenLoading()
        }

        if (state.error != null) {
            HandleError(
                error = state.error!!, // Not null
                onPositiveAction = onPositiveAction,
                onNegativeAction = onNegativeAction,
                onShowSnackBar = onShowSnackBar,
                onDismissRequest = onDismissErrorDialog,
            )
        }
    }
}

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                ),
        ), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.background(Color.Transparent),
            strokeWidth = 5.dp,
        )
    }
}

@Composable
fun HandleError(
    error: WeatherException,
    onPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onNegativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onShowSnackBar: (message: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val context = LocalContext.current

    when (error) {
        is WeatherException.AlertException -> {
            WeatherAlertDialog(
                alertDialog = error.alertDialog,
                onDismissRequest = onDismissRequest,
                positiveAction = onPositiveAction,
                negativeAction = onNegativeAction,
            )
        }

        else -> {
            LaunchedEffect(key1 = true) {
                onShowSnackBar.invoke(error.message ?: context.getString(R.string.error_message_default))
                onDismissRequest.invoke()
            }
        }
    }
}

@Composable
fun WeatherAlertDialog(
    alertDialog: AlertDialog,
    positiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    negativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = alertDialog.title)
        },
        text = {
            Text(text = alertDialog.message)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    positiveAction.invoke(alertDialog.positiveAction, alertDialog.positiveObject)
                },
            ) {
                Text(text = alertDialog.positiveMessage ?: stringResource(id = R.string.retry))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    negativeAction.invoke(alertDialog.positiveAction, alertDialog.positiveObject)
                },
            ) {
                Text(text = alertDialog.negativeMessage ?: stringResource(id = android.R.string.cancel))
            }
        },
    )
}
