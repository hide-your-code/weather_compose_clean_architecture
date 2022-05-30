package com.minhdtm.example.weapose.domain.enums

/**
 * Clear exception from Throwable
 * @param SNACK is type of show message via Snack bar
 * @param TOAST is type of show message via Toast
 * @param INLINE is type of show or hide view warning, example: password in correct hint of password field
 * @param ALERT_DIALOG is type of show Alert Dialog, with multiple attributes: title, message, positive, negative & action
 * @param REDIRECT is type of auto-redirect with view, action or finished, ...
 * @param ON_PAGE is type of show message on center screen, maybe show retry button
 */
enum class ExceptionType {
    SNACK, TOAST, INLINE, ALERT_DIALOG, REDIRECT, ON_PAGE,
}
