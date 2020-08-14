package com.asarkar.kotlinx.test

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Coroutines timeout annotation that can be applied to a class and/or individual test methods. Method level annotation
 * overrides class level annotation.
 * This annotation runs the test on a separate thread, and fails it if it exceeds the given timeout.
 * Additionally, this annotation installs [DebugProbes](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-debug/kotlinx.coroutines.debug/-debug-probes/index.html)
 * and dumps all coroutines on timeout failure.
 *
 * Example of usage:
 * ```
 * @CoroutinesTimeout(timeout = "PT1S")
 * class TimeConsumingTest {
 *    @CoroutinesTimeout(timeout = "PT2S")
 *    @Test
 *    fun testOverrideTimeout() {
 *        runBlocking { delay(1500) }
 *    }
 *
 *    @Test
 *    fun test() {
 *        runBlocking { delay(800) }
 *    }
 * }
 * ```
 * @property timeout [ISO 8601 Duration](https://en.wikipedia.org/wiki/ISO_8601#Durations) format string.
 * @property cancelOnTimeout Whether suspended coroutines should be cancelled on timeout. Note that the stacktrace dump, if enabled, is taken prior to the cancellations.
 * @property enableCreationStackTraces See [DebugProbes.enableCreationStackTraces](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-debug/kotlinx.coroutines.debug/-debug-probes/enable-creation-stack-traces.html).
 * @property sanitizeStackTraces See [DebugProbes.sanitizeStackTraces](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-debug/kotlinx.coroutines.debug/-debug-probes/sanitize-stack-traces.html).
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Tags(Tag("coroutines"), Tag("timeout"))
@ExtendWith(CoroutinesTimeoutExtension::class)
annotation class CoroutinesTimeout(
    val timeout: String,
    val cancelOnTimeout: Boolean = false,
    val enableCreationStackTraces: Boolean = true,
    val sanitizeStackTraces: Boolean = false
)
