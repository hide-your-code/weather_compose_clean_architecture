package com.minhdtm.example.weapose.domain.enums

/**
 * Clear exception from Throwable
 * [ExceptionType.Snack] is type of show message via Snack bar
 * [ExceptionType.Toast] is type of show message via Toast
 * [ExceptionType.Inline] is type of show or hide view warning, example: password in correct hint of password field
 * [ExceptionType.AlertDialog] is type of show Alert Dialog, with multiple attributes: title, message, positive, negative & action
 * [ExceptionType.Redirect] is type of auto-redirect with view, action or finished, ...
 * [ExceptionType.OnPage] is type of show message on center screen, maybe show retry button
 */
sealed interface ExceptionType {
    object Snack : ExceptionType

    object Toast : ExceptionType

    object Inline : ExceptionType

    object AlertDialog : ExceptionType

    object Redirect : ExceptionType

    object OnPage : ExceptionType
}
