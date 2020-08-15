package com.asarkar.kotlinx.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionConfigurationException
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import org.junit.platform.commons.JUnitException
import org.junit.platform.commons.support.AnnotationSupport
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.reflect.Method
import java.time.Duration
import java.time.format.DateTimeParseException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A JUnit 5 [Extension](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/Extension.html)
 * that backs the [CoroutinesTimeout] annotation.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
class CoroutinesTimeoutExtension : BeforeAllCallback, InvocationInterceptor {
    companion object {
        private val CLASSNAME = CoroutinesTimeoutExtension::class.java.name
        private val NAMESPACE: ExtensionContext.Namespace = ExtensionContext.Namespace
            .create(*CLASSNAME.split(".").toTypedArray())
        private const val CLASS_ANNOTATION = "class-annotation"
        internal val CAPTURE_STDOUT = "$CLASSNAME.capture-stdout"
    }

    override fun beforeAll(context: ExtensionContext) {
        context.getStore(NAMESPACE).put(
            CLASS_ANNOTATION,
            context.testClass
                .flatMap { AnnotationSupport.findAnnotation(it, CoroutinesTimeout::class.java) }
                .orElse(null)
        )
    }

    @ExperimentalCoroutinesApi
    override fun interceptTestMethod(
        invocation: InvocationInterceptor.Invocation<Void>,
        invocationContext: ReflectiveInvocationContext<Method>?,
        extensionContext: ExtensionContext
    ) {
        val annotation = extensionContext.element
            .flatMap { AnnotationSupport.findAnnotation(it, CoroutinesTimeout::class.java) }
            .orElse(
                extensionContext.getStore(NAMESPACE)
                    .get(CLASS_ANNOTATION, CoroutinesTimeout::class.java)
            )

        if (annotation == null) invocation.proceed()
        else {
            val timeout = try {
                Duration.parse(annotation.timeout)
            } catch (e: DateTimeParseException) {
                throw ExtensionConfigurationException("${annotation.timeout} cannot be parsed to a duration", e)
            }

            DebugProbes.apply {
                enableCreationStackTraces = annotation.enableCreationStackTraces
                sanitizeStackTraces = annotation.sanitizeStackTraces
                install()
            }

            // For testing
            val captureStdout = extensionContext.getConfigurationParameter(CAPTURE_STDOUT)
                .isPresent
            val (baos, stdout) =
                if (captureStdout) ByteArrayOutputStream().let { it to PrintStream(it) } else null to System.out

            val testStartedLatch = CountDownLatch(1)
            val testResult = FutureTask<Unit> {
                testStartedLatch.countDown()
                invocation.proceed()
            }

            val testThread = Thread(testResult, "Timeout test thread").apply { isDaemon = true }
            try {
                testThread.start()
                // Await until test is started to take only test execution time into account
                testStartedLatch.await()
                testResult.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
            } catch (e: TimeoutException) {
                DebugProbes.dumpCoroutines(stdout)
                /*
                 * Order is important:
                 * 1) Create exception with a stacktrace of hung test
                 * 2) Cancel all coroutines
                 * 3) Throw created exception
                 */
                val ex = JUnitException("${extensionContext.displayName} timed out after ${timeout.toMillis()} ms")
                ex.stackTrace = testThread.stackTrace
                cancelIfNecessary(annotation.cancelOnTimeout)
                stdout.flush()
                throw ex
            } catch (e: ExecutionException) {
                throw e.cause ?: e
            } finally {
                testThread.interrupt()
                DebugProbes.uninstall()
                if (captureStdout) {
                    // Publishing empty value throws exception
                    baos?.takeIf { it.size() > 0 }?.also {
                        extensionContext.publishReportEntry("dump", it.toString("UTF-8"))
                        it.close()
                    }
                    stdout.close()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun cancelIfNecessary(cancelOnTimeout: Boolean) {
        if (cancelOnTimeout) {
            DebugProbes.dumpCoroutinesInfo().forEach {
                it.job?.cancel()
            }
        }
    }
}
