import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.pluginPublish)
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

publishing {
    publications {
        create<MavenPublication>("kspProcessor") {
            from(components["java"])

            groupId = "io.github.fpiechowski.arrowgen"
            artifactId = "arrow-gen-processor"
            version = "0.0.1"

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
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        findProperty("signing.secretKeyRingFile")?.let { File(it.toString()).readText() },
        findProperty("signing.password") as String?,
    )
    sign(publishing.publications)
}
