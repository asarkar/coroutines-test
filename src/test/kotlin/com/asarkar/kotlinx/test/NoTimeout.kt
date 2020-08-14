package com.asarkar.kotlinx.test

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class NoTimeout {
    @Test
    fun test() {
        runBlocking { assert(true) }
    }
}
