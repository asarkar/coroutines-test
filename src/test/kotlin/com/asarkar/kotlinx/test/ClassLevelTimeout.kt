package com.asarkar.kotlinx.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@CoroutinesTimeout(timeout = "PT0.5S")
class ClassLevelTimeout {
    @Test
    fun test() {
        runBlocking { assert(true) }
    }

    @Test
    fun testTimeout() {
        runBlocking { delay(2000) }
    }
}
