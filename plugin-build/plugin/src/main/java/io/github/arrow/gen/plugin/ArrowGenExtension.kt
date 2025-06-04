package io.github.arrow.gen.plugin

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class ArrowGenExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        val include: ListProperty<String> =
            objects
                .listProperty(String::class.java)
                .convention(emptyList())

        val exclude: ListProperty<String> =
            objects
                .listProperty(String::class.java)
                .convention(emptyList())

        val raise: Property<Boolean> =
            objects
                .property(Boolean::class.java)
                .convention(false)

        val either: Property<Boolean> =
            objects
                .property(Boolean::class.java)
                .convention(false)
    }
