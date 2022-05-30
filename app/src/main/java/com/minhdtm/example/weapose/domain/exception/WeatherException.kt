package com.minhdtm.example.weapose.domain.exception

import com.minhdtm.example.weapose.domain.enums.ExceptionType
import com.minhdtm.example.weapose.domain.enums.RedirectType
import com.minhdtm.example.weapose.domain.model.AlertDialog
import com.minhdtm.example.weapose.domain.model.Tag

sealed class WeatherException(
    open val code: Int,
    val type: ExceptionType,
    override val message: String?,
) : Throwable(message) {

    data class AlertException(
        override val code: Int,
        val alertDialog: AlertDialog,
    ) : WeatherException(code, ExceptionType.ALERT_DIALOG, null)

    data class InlineException(
        override val code: Int,
        val tags: List<Tag>,
    ) : WeatherException(code, ExceptionType.INLINE, null)

    data class RedirectException(
        override val code: Int,
        val redirect: RedirectType,
    ) : WeatherException(code, ExceptionType.REDIRECT, null)

    data class SnackBarException(
        override val code: Int = -1,
        override val message: String,
    ) : WeatherException(code, ExceptionType.SNACK, message)

    data class ToastException(
        override val code: Int,
        override val message: String,
    ) : WeatherException(code, ExceptionType.TOAST, message)

    data class OnPageException(
        override val code: Int,
        override val message: String,
    ) : WeatherException(code, ExceptionType.ON_PAGE, message)
}
