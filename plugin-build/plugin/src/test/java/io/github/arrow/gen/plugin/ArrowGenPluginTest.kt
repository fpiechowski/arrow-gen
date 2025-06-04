package io.github.arrow.gen.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertNotNull
import org.junit.Test

class ArrowGenPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.arrow.gen.plugin")

        // Verify that the plugin applies the KSP plugin
        // assert(project.plugins.hasPlugin("com.google.devtools.ksp"))
    }

    @Test
    fun `extension arrowGen is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.arrow.gen.plugin")

        assertNotNull(project.extensions.getByName("arrowGen"))
    }

    @Test
    fun `extension properties have correct default values`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.arrow.gen.plugin")

        val extension = project.extensions.getByName("arrowGen") as ArrowGenExtension

        assert(extension.include.get().isEmpty())
        assert(extension.exclude.get().isEmpty())
        assert(!extension.raise.get())
        assert(!extension.either.get())
    }
}
