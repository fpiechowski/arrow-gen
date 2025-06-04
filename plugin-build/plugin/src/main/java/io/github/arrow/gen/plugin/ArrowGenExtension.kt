package io.github.arrow.gen.plugin

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Extension for Arrow extension function generator plugin.
 * Allows configuration of package filters and generation options.
 */
@Suppress("UnnecessaryAbstractClass")
abstract class ArrowGenExtension
    @Inject
    constructor(project: Project) {
        private val objects = project.objects

        /**
         * List of package patterns to include in the generation.
         * Supports wildcards, e.g. "com.example.*", "org.sample.api.**".
         */
        val include: ListProperty<String> = objects.listProperty(String::class.java)
            .convention(emptyList())

        /**
         * List of package patterns to exclude from the generation.
         * Supports wildcards, e.g. "com.example.internal.*".
         */
        val exclude: ListProperty<String> = objects.listProperty(String::class.java)
            .convention(emptyList())

        /**
         * Whether to generate Raise<T: Throwable>.(...) -> T extension functions.
         */
        val raise: Property<Boolean> = objects.property(Boolean::class.java)
            .convention(false)

        /**
         * Whether to generate functions returning Either<E, T>.
         */
        val either: Property<Boolean> = objects.property(Boolean::class.java)
            .convention(false)
    }
