package io.github.arrow.gen.plugin

import io.github.classgraph.ClassGraph
import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "arrowGen"

/**
 * Gradle plugin for generating Arrow extension functions.
 * This plugin integrates with KSP to process Kotlin source files and generate
 * extension functions that wrap specified APIs with Arrow's error handling capabilities.
 */
class ArrowGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, ArrowGenExtension::class.java)

        project.pluginManager.withPlugin("java") {
            // Register the scanning task after evaluation (to ensure include/exclude are configured)
            project.afterEvaluate {
                val includePatterns = extension.include.getOrElse(emptyList())
                val excludePatterns = extension.exclude.getOrElse(emptyList())

                val runtimeClasspath = project.configurations.getByName("runtimeClasspath")

                val outputDir = project.layout.buildDirectory.dir("generated").get()
                val outputFile = outputDir.file("arrowGenScan")

                project.tasks.register("arrowGenScan") {
                    it.doLast {
                        outputDir.asFile.mkdirs()

                        val classGraph = ClassGraph()
                            .overrideClasspath(runtimeClasspath)
                            .enableClassInfo()
                            .ignoreClassVisibility()

                        val scanResult = classGraph.scan()
                        val allClasses = scanResult.allClasses

                        val matchesInclude = { name: String ->
                            includePatterns.isEmpty() || includePatterns.any { pattern ->
                                globMatches(name, pattern)
                            }
                        }

                        val matchesExclude = { name: String ->
                            excludePatterns.any { pattern ->
                                globMatches(name, pattern)
                            }
                        }

                        val matchingFqns = allClasses
                            .map { it.name }
                            .filter { matchesInclude(it) && !matchesExclude(it) }
                            .sorted()

                        outputFile.asFile.writeText(matchingFqns.joinToString("\n"))
                        println("Scanned FQNs written to: ${outputFile.asFile.absolutePath}")
                    }
                }
            }
        }

        project.plugins.withId("com.google.devtools.ksp") {
            val kspExt = project.extensions.findByName("ksp")
            val argMethod = kspExt?.javaClass?.methods
                ?.find { it.name == "arg" && it.parameterCount == 2 }

            argMethod?.invoke(kspExt, "arrowGen.raise", extension.raise.get().toString())
            argMethod?.invoke(kspExt, "arrowGen.either", extension.either.get().toString())
        }
    }

    private fun globMatches(fqn: String, pattern: String): Boolean {
        // Convert pattern like "com.example.**" to regex
        val regex = pattern
            .replace(".", "\\.")
            .replace("**", ".*")
            .replace("*", "[^.]*")
            .toRegex()
        return regex.matches(fqn)
    }
}
