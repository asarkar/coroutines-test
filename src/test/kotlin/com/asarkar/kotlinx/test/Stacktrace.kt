package com.asarkar.kotlinx.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class Stacktrace {
    @Test
    @CoroutinesTimeout(timeout = "PT0.5S")
    fun test() {
        runBlocking { delay(1000) }
    }

    @Test
    @CoroutinesTimeout(timeout = "PT0.5S", enableCreationStackTraces = false)
    fun testNoStacktrace() {
        runBlocking { delay(1000) }
    }
}
