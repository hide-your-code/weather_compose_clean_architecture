package com.minhdtm.example.weapose.domain.model

import com.minhdtm.example.weapose.domain.enums.TagType

data class Tag(
    val name: TagType,
    val message: String?,
)
