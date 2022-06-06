package com.minhdtm.example.weapose.presentation.component

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import timber.log.Timber

private suspend fun infinityTextChanged(
    texts: List<String>,
    delayTime: Long = 3000L,
    onTextChange: (String) -> Unit,
): String {
    var item = 0

    while (true) {
        delay(delayTime)

        item = if (item == texts.size - 1) {
            0
        } else {
            item + 1
        }
        onTextChange.invoke(texts[item])
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InfinityText(
    modifier: Modifier = Modifier,
    texts: List<String>,
    delayTime: Long = 3000L,
    content: @Composable (targetState: String) -> Unit = {},
) {
    if (texts.isNotEmpty()) {
        var text by rememberSaveable {
            mutableStateOf(texts.first())
        }

        LaunchedEffect(texts) {
            infinityTextChanged(texts, delayTime) {
                text = it
            }
        }

        AnimatedContent(
            modifier = modifier,
            targetState = text,
            transitionSpec = {
                slideInVertically { height -> -height } + fadeIn() with slideOutVertically { height -> height } + fadeOut()
            },
        ) { targetState ->
            content.invoke(targetState)
        }
    }
}
