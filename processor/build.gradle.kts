import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.pluginPublish)
    id("com.vanniktech.maven.publish") version "0.32.0"
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    implementation(kotlin("stdlib"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("io.github.fpiechowski.arrowgen", "arrowgen-processor", "0.0.1")

    pom {
        name.set("ArrowGen Processor")
        description.set("KSP processor to generate Raise, Either, Effect wrappers.")
        url.set("https://github.com/fpiechowski/arrow-gen")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("fpiechowski")
                name.set("Filip Piechowski")
                email.set("f.piechowski@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/fpiechowski/arrow-gen.git")
            developerConnection.set("scm:git:ssh://github.com:fpiechowski/arrow-gen.git")
            url.set("https://github.com/fpiechowski/arrow-gen")
        }
    }
}
