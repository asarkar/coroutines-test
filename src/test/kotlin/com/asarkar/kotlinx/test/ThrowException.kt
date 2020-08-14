package com.asarkar.kotlinx.test

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@CoroutinesTimeout("PT1S")
class ThrowException {
    @Test
    fun test() {
        runBlocking<Nothing> { throw NullPointerException("Boom!") }
    }
}
