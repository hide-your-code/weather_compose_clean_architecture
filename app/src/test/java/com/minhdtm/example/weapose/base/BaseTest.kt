package com.minhdtm.example.weapose.base

import org.junit.Rule

open class BaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
}
