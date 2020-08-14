package com.asarkar.kotlinx.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@CoroutinesTimeout(timeout = "PT1S")
class MethodLevelTimeout {
    @Test
    fun test() {
        runBlocking { assert(true) }
    }

    @CoroutinesTimeout(timeout = "PT2S")
    @Test
    fun testOverrideTimeout() {
        runBlocking { delay(1500) }
    }

    @CoroutinesTimeout(timeout = "PT0.5S")
    @Test
    fun testTimeout() {
        runBlocking { delay(800) }
    }
}
