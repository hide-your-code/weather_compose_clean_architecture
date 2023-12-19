package com.minhdtm.example.weapose.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

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
                val fadeInAndFadeOut =
                    (slideInVertically { height -> -height } + fadeIn())
                        .togetherWith(slideOutVertically { height -> height } + fadeOut())
                fadeInAndFadeOut.using(
                    SizeTransform(clip = false)
                )
            },
            label = "",
        ) { targetState ->
            content.invoke(targetState)
        }
    }
}
