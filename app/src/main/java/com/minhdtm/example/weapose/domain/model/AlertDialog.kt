package com.minhdtm.example.weapose.domain.model

import com.minhdtm.example.weapose.domain.enums.ActionType

data class AlertDialog(
    val title: String,
    val message: String,
    val positiveMessage: String? = null,
    val positiveAction: ActionType? = null,
    val positiveObject: Any? = null,
    val negativeMessage: String? = null,
    val negativeAction: ActionType? = null,
    val negativeObject: Any? = null
)
