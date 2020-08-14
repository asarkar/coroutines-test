# coroutines-test

Includes a JUnit 5 [Extension](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/Extension.html) that can dump coroutines on timeout.
Like [CoroutinesTimeout](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-debug/kotlinx.coroutines.debug.junit4/-coroutines-timeout/index.html) JUnit 4 rule, 
but built for JUnit 5 and with almost 100% test coverage (yes, I test code that's meant to test others' code).

Requires Java 8 or later. Verified working with the latest JUnit 5 version, which you can find in the [gradle.properties](gradle.properties).

[![](https://github.com/asarkar/coroutines-test/workflows/CI%20Pipeline/badge.svg)](https://github.com/asarkar/coroutines-test/actions?query=workflow%3A%22CI+Pipeline%22)

## Installation

You can find the latest version on Bintray. [ ![Download](https://api.bintray.com/packages/asarkar/mvn/com.asarkar.kotlinx%3Acoroutines-test/images/download.svg) ](https://bintray.com/asarkar/mvn/com.asarkar.kotlinx%3Acoroutines-test/_latestVersion)

It is also on Maven Central and jcenter.

## Usage

See KDoc.

## Contribute

This project is a volunteer effort. You are welcome to send pull requests, ask questions, or create issues.
If you like it, you can help by spreading the word!

## License

Copyright 2020 Abhijit Sarkar - Released under [Apache License v2.0](LICENSE).
