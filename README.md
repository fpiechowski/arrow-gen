# Arrow Extension Function Generator

[![License](https://img.shields.io/github/license/cortinico/kotlin-android-template.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin)

A Gradle plugin that generates Arrow extension functions for specified APIs. The plugin uses KSP (Kotlin Symbol Processing) to generate extension functions that wrap regular functions with Arrow's error handling capabilities.

This plugin allows you to configure what kind of Arrow API to generate:
- `raise` flag enables generation of `Raise<T: Throwable>.(...) -> T` extension functions
- `either` flag enables generation of functions returning `Either<E, T>`
- `effect` flag enables generation of functions returning `Effect<E, T>`

Generated functions call source functions wrapped in Arrow's `catch` block.

## How to use 👣

### Setup

Add the plugin to your project's build.gradle.kts file:

```kotlin
plugins {
    id("io.github.fpiechowski.arrowgen") version "0.0.1"
}
```

### Configuration

Configure the plugin in your build.gradle.kts file:

```kotlin
arrowGen {
    // Include packages to process (supports wildcards)
    include.set(listOf(
        "com.example.api.*",      // Single-level wildcard
        "org.sample.service.**"   // Multi-level wildcard
    ))

    // Exclude packages from processing (supports wildcards)
    excludePackages.set(listOf(
        "com.example.api.internal.*"
    ))

    // Enable generation of Raise<T: Throwable>.(...) -> T extension functions
    raise.set(true)

    // Enable generation of functions returning Either<E, T>
    either.set(true)
}
```

### Generated Code

For each function that matches your package filters, the plugin will generate extension functions in a new package with the `.arrow` suffix.

For example, if you have a function:

```kotlin
package com.example.api

fun fetchData(id: String): Data {
    // Implementation that might throw exceptions
}
```

With both `raise` and `either` enabled, the plugin will generate:

```kotlin
package com.example.api.arrow

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.example.api.fetchData

// Generated when raise=true
suspend fun <E> Raise<E>.fetchData(id: String): Data {
    return catch(
        block = { fetchData(id) },
        raise = { e: Throwable -> raise(e as E) }
    )
}

// Generated when either=true
suspend fun <E : Throwable> fetchDataEither(id: String): Either<E, Data> {
    return try {
        fetchData(id).right()
    } catch (e: Throwable) {
        (e as E).left()
    }
}
```

## Features 🎨

- **100% Kotlin implementation** using KSP (Kotlin Symbol Processing).
- **Flexible package filtering** with wildcard support for inclusion and exclusion.
- **Arrow integration** for functional error handling:
  - `Raise<E>` extension functions for Railway-oriented programming.
  - `Either<E, T>` return types for explicit error handling.
- **Automatic code generation** that wraps existing APIs without modifying source code.
- **Type-safe configuration** via Gradle extension.
- **Minimal runtime dependencies** - only requires Arrow Core.

## Composite Build 📦

This template is using a [Gradle composite build](https://docs.gradle.org/current/userguide/composite_builds.html) to build, test and publish the plugin. This means that you don't need to run Gradle twice to test the changes on your Gradle plugin (no more `publishToMavenLocal` tricks or so).

The included build is inside the [plugin-build](plugin-build) folder.

### `preMerge` task

A `preMerge` task on the top level build is already provided in the template. This allows you to run all the `check` tasks both in the top level and in the included build.

You can easily invoke it with:

```
./gradlew preMerge
```

If you need to invoke a task inside the included build with:

```
./gradlew -p plugin-build <task-name>
```


### Dependency substitution

Please note that the project relies on module name/group in order for [dependency substitution](https://docs.gradle.org/current/userguide/resolution_rules.html#sec:dependency_substitution_rules) to work properly. If you change only the plugin ID everything will work as expected. If you change module name/group, things might break and you probably have to specify a [substitution rule](https://docs.gradle.org/current/userguide/resolution_rules.html#sub:project_to_module_substitution).


## Publishing 🚀

This template is ready to let you publish to [Gradle Portal](https://plugins.gradle.org/).

The [![Publish Plugin to Portal](https://github.com/cortinico/kotlin-gradle-plugin-template/workflows/Publish%20Plugin%20to%20Portal/badge.svg?branch=1.0.0)](https://github.com/cortinico/kotlin-gradle-plugin-template/actions?query=workflow%3A%22Publish+Plugin+to+Portal%22) Github Action will take care of the publishing whenever you **push a tag**.

Please note that you need to configure two secrets: `GRADLE_PUBLISH_KEY` and `GRADLE_PUBLISH_SECRET` with the credentials you can get from your profile on the Gradle Portal.

## 100% Kotlin 🅺

This template is designed to use Kotlin everywhere. The build files are written using [**Gradle Kotlin DSL**](https://docs.gradle.org/current/userguide/kotlin_dsl.html) as well as the [Plugin DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block) to setup the build.

Dependencies are centralized inside the [libs.versions.toml](gradle/libs.versions.toml).

Moreover, a minimalistic Gradle Plugin is already provided in Kotlin to let you easily start developing your own around it.

## Static Analysis 🔍

This template is using [**ktlint**](https://github.com/pinterest/ktlint) with the [ktlint-gradle](https://github.com/jlleitschuh/ktlint-gradle) plugin to format your code. To reformat all the source code as well as the buildscript you can run the `ktlintFormat` gradle task.

This template is also using [**detekt**](https://github.com/arturbosch/detekt) to analyze the source code, with the configuration that is stored in the [detekt.yml](config/detekt/detekt.yml) file (the file has been generated with the `detektGenerateConfig` task).

## CI ⚙️

This template is using [**GitHub Actions**](https://github.com/cortinico/kotlin-android-template/actions) as CI. You don't need to setup any external service and you should have a running CI once you start using this template.

There are currently the following workflows available:
- [Validate Gradle Wrapper](.github/workflows/gradle-wrapper-validation.yml) - Will check that the gradle wrapper has a valid checksum
- [Pre Merge Checks](.github/workflows/pre-merge.yaml) - Will run the `preMerge` tasks as well as trying to run the Gradle plugin.
- [Publish to Plugin Portal](.github/workflows/publish-plugin.yaml) - Will run the `publishPlugin` task when pushing a new tag.

## Contributing 🤝

Feel free to open a issue or submit a pull request for any bugs/improvements.

## License 📄

This template is licensed under the MIT License - see the [License](LICENSE) file for details.
Please note that the generated template is offering to start with a MIT license but you can change it to whatever you wish, as long as you attribute under the MIT terms that you're using the template.
