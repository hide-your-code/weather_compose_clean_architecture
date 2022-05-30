package com.minhdtm.example.weapose.domain.model

import com.minhdtm.example.weapose.domain.enums.RedirectType

data class Redirect(
    val redirect: RedirectType,
    val redirectObject: Any? = null,
)
