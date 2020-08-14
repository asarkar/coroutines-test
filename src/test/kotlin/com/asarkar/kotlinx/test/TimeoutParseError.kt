package com.asarkar.kotlinx.test

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@CoroutinesTimeout("junk")
class TimeoutParseError {
    @Test
    fun test() {
        runBlocking { assert(true) }
    }
}
