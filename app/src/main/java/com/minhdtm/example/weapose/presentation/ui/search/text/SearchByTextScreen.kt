package com.minhdtm.example.weapose.presentation.ui.search.text

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.component.InfinityText
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.model.HistorySearchAddressViewData
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.clearFocusOnKeyboardDismiss

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalLifecycleComposeApi::class,
)
@Composable
fun SearchByText(
    appState: WeatherAppState,
    viewModel: SearchByTextViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var text by rememberSaveable {
        mutableStateOf("")
    }

    // Get event
    LaunchedEffect(state) {
        val navigateToSearchByMap = state.navigateToSearchByMap

        when {
            navigateToSearchByMap != null -> {
                appState.navigateToSearchByMap(navigateToSearchByMap.fromRoute, navigateToSearchByMap.latLng)
            }
            else -> return@LaunchedEffect
        }

        viewModel.cleanEvent()
    }

    // Hide keyboard when SearchByText is disposed
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(true) {
        onDispose {
            keyboardController?.hide()
        }
    }

    // Change the placeholder text when switch to vietnamese or english
    val context = LocalContext.current
    val locale = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    LaunchedEffect(locale) {
        viewModel.updatePlaceHolder(context.getString(R.string.search_every_where_you_want))
    }

    SearchByTextScreen(
        state = state,
        text = text,
        onTextChange = {
            text = it
            viewModel.getAddress(it)
        },
        onClickBack = {
            appState.popBackStack()
        },
        onClickMap = {
            viewModel.onNavigateToSearchByMap()
        },
        onClickResultSearch = {
            val addressName = it.getPrimaryText(null).toString()
            viewModel.addSearchHistory(addressName)

            val params = mutableMapOf<String, Any>()
            params[Constants.Key.ADDRESS_NAME] = it.getPrimaryText(null).toString()
            appState.popBackStack(params = params)
        },
        onClickHistory = {
            val params = mutableMapOf<String, Any>()
            params[Constants.Key.ADDRESS_NAME] = it.address
            appState.popBackStack(params = params)
        },
        onClearAllHistory = {
            viewModel.clearHistory()
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByTextScreen(
    state: SearchByTextViewState,
    text: String = "",
    onTextChange: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickMap: () -> Unit = {},
    onClickResultSearch: (AutocompletePrediction) -> Unit = {},
    onClearAllHistory: () -> Unit = {},
    onClickHistory: (HistorySearchAddressViewData) -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
) {
    WeatherScaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        state = state,
        onDismissErrorDialog = onDismissErrorDialog,
        topBar = {
            SearchByTextAppBar(
                text = text,
                placeholder = state.addressPlaceHolder,
                onTextChange = onTextChange,
                onClickBack = onClickBack,
                onClickMap = onClickMap,
            )
        },
    ) { _, _ ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Title(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                        .fillMaxWidth(),
                    textTitle = stringResource(id = R.string.history),
                    textAction = stringResource(id = R.string.clear_all),
                    onClickAction = onClearAllHistory,
                )
            }

            if (state.listSearch.isEmpty()) {
                item {
                    EmptyList(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.no_history),
                    )
                }
            } else {
                item {
                    ListHistorySearch(
                        modifier = Modifier.fillMaxWidth(),
                        onClickHistoryItem = onClickHistory,
                        listHistory = state.listSearch,
                    )
                }
            }

            item {
                Title(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                        .fillMaxWidth(),
                    textTitle = stringResource(id = R.string.result),
                )
            }

            if (state.listResult.isEmpty()) {
                item {
                    EmptyList(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.no_result),
                    )
                }
            } else {
                items(items = state.listResult) { item ->
                    ItemResultSearch(
                        modifier = Modifier.fillMaxWidth(),
                        item = item,
                        onClickResultSearch = onClickResultSearch,
                    )
                }
            }
        }
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    textTitle: String,
    textAction: String = "",
    onClickAction: () -> Unit = {},
) {
    Row(
        modifier = modifier.height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = textTitle,
            style = MaterialTheme.typography.titleLarge,
        )

        if (textAction.isNotBlank()) {
            TextButton(onClick = onClickAction) {
                Text(text = textAction)
            }
        }
    }
}

@Composable
fun EmptyList(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier.height(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListHistorySearch(
    modifier: Modifier = Modifier,
    listHistory: List<HistorySearchAddressViewData> = emptyList(),
    onClickHistoryItem: (HistorySearchAddressViewData) -> Unit = {},
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 10.dp),
        mainAxisSpacing = 10.dp,
    ) {
        listHistory.forEach {
            ElevatedAssistChip(
                onClick = {
                    onClickHistoryItem(it)
                },
                label = {
                    Text(text = it.address)
                },
            )
        }
    }
}

@Composable
fun ItemResultSearch(
    modifier: Modifier = Modifier,
    item: AutocompletePrediction,
    onClickResultSearch: (AutocompletePrediction) -> Unit = {},
) {
    Column(
        modifier = modifier
            .height(66.dp)
            .clickable {
                onClickResultSearch.invoke(item)
            },
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = item.getPrimaryText(null).toString(),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp),
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = item.getSecondaryText(null).toString(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByTextAppBar(
    modifier: Modifier = Modifier,
    placeholder: List<String> = emptyList(),
    text: String = "",
    onTextChange: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickMap: () -> Unit = {},
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .clearFocusOnKeyboardDismiss(),
        value = text,
        onValueChange = onTextChange,
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            IconButton(onClick = onClickBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        trailingIcon = {
            IconButton(onClick = onClickMap) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
            }
        },
        placeholder = {
            InfinityText(
                texts = placeholder,
                delayTime = 4000L,
                content = { targetState ->
                    Text(
                        text = targetState,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        },
    )
}
