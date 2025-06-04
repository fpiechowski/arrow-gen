package io.github.fpiechowski.arrowgen.plugin

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "arrowGen"

class ArrowGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, ArrowGenExtension::class.java)

        project.afterEvaluate {
            project.plugins.withId("com.google.devtools.ksp") {
                project.extensions.configure(KspExtension::class.java) { kspExtension ->
                    kspExtension.arg("arrowGen.raise", extension.raise.get().toString())
                    kspExtension.arg("arrowGen.either", extension.either.get().toString())
                    kspExtension.arg("arrowGen.effect", extension.effect.get().toString())
                    kspExtension.arg("arrowGen.include", extension.include.get().joinToString(","))
                    kspExtension.arg("arrowGen.exclude", extension.exclude.get().joinToString(","))
                }
            }
        }
    }
}
