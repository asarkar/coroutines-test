package com.asarkar.kotlinx.test

import com.asarkar.kotlinx.test.CoroutinesTimeoutExtension.Companion.CAPTURE_STDOUT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionConfigurationException
import org.junit.platform.commons.JUnitException
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.EventConditions
import org.junit.platform.testkit.engine.EventType
import org.junit.platform.testkit.engine.TestExecutionResultConditions

class CoroutinesTimeoutExtensionIntegrationTests {
    @Test
    fun testClassLevelTimeout() {
        val results = EngineTestKit.engine("junit-jupiter")
            .selectors(
                DiscoverySelectors.selectClass(
                    ClassLevelTimeout::class.java
                )
            )
            .execute()

        results
            .testEvents()
            .assertThatEvents()
            .haveExactly(
                1,
                EventConditions.finishedSuccessfully()
            )

        results
            .testEvents()
            .assertThatEvents()
            .haveExactly(
                1,
                EventConditions.finishedWithFailure(
                    TestExecutionResultConditions.instanceOf(
                        JUnitException::class.java
                    )
                )
            )
    }

    @Test
    fun testMethodLevelTimeout() {
        val events = EngineTestKit.engine("junit-jupiter")
            .selectors(
                DiscoverySelectors.selectClass(
                    MethodLevelTimeout::class.java
                )
            )
            .execute()
            .testEvents()

        events
            .assertThatEvents()
            .haveExactly(
                2,
                EventConditions.finishedSuccessfully()
            )

        events
            .assertThatEvents()
            .haveExactly(
                1,
                EventConditions.finishedWithFailure(
                    TestExecutionResultConditions.instanceOf(
                        JUnitException::class.java
                    )
                )
            )

        events.failed()
            .list()
            .map { it.toString() }
            .forEach(::println)
    }

    @Test
    fun testNoTimeout() {
        EngineTestKit.engine("junit-jupiter")
            .selectors(
                DiscoverySelectors.selectClass(
                    NoTimeout::class.java
                )
            )
            .execute()
            .testEvents()
            .assertThatEvents()
            .haveExactly(1, EventConditions.finishedSuccessfully())
    }

    @Test
    fun testStacktrace() {
        val stacktrace = EngineTestKit.engine("junit-jupiter")
            .configurationParameter(CAPTURE_STDOUT, "doesnotmatter")
            .selectors(
                DiscoverySelectors.selectMethod(
                    Stacktrace::class.java,
                    Stacktrace::class.java.getMethod("test")
                )
            )
            .execute()
            .allEvents()
            .list()
            .first { it.type == EventType.REPORTING_ENTRY_PUBLISHED }
            .payload.toString().split(System.lineSeparator())
            .drop(2) // Drop header and blank line

        assertThat(stacktrace).hasSizeGreaterThan(2)
        assertThat(stacktrace.first()).endsWith("SUSPENDED")
        assertThat(stacktrace[1]).contains(Stacktrace::class.simpleName)
    }

    @Test
    fun testNoStacktrace() {
        val stacktrace = EngineTestKit.engine("junit-jupiter")
            .configurationParameter(CAPTURE_STDOUT, "doesnotmatter")
            .selectors(
                DiscoverySelectors.selectMethod(
                    Stacktrace::class.java,
                    Stacktrace::class.java.getMethod("testNoStacktrace")
                )
            )
            .execute()
            .allEvents()
            .list()
            .first { it.type == EventType.REPORTING_ENTRY_PUBLISHED }
            .payload.toString().split(System.lineSeparator())
            .drop(2) // Drop header and blank line

        assertThat(stacktrace).hasSize(2)
        assertThat(stacktrace.first()).endsWith("SUSPENDED")
        assertThat(stacktrace[1]).contains(Stacktrace::class.simpleName)
    }

    @Test
    fun testTimeoutParseError() {
        EngineTestKit.engine("junit-jupiter")
            .selectors(
                DiscoverySelectors.selectClass(
                    TimeoutParseError::class.java
                )
            )
            .execute()
            .testEvents()
            .assertThatEvents()
            .haveExactly(
                1,
                EventConditions.finishedWithFailure(
                    TestExecutionResultConditions.instanceOf(
                        ExtensionConfigurationException::class.java
                    )
                )
            )
    }

    @Test
    fun testThrowException() {
        EngineTestKit.engine("junit-jupiter")
            .selectors(
                DiscoverySelectors.selectClass(
                    ThrowException::class.java
                )
            )
            .execute()
            .testEvents()
            .assertThatEvents()
            .haveExactly(
                1,
                EventConditions.finishedWithFailure(
                    TestExecutionResultConditions.instanceOf(
                        NullPointerException::class.java
                    )
                )
            )
    }
}
