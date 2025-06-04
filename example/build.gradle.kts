plugins {
    kotlin("jvm") version "2.1.21"
    id("io.github.arrow.gen.plugin") version "1.0.0"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.arrow-kt:arrow-core:2.1.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.1.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    ksp(project(":processor"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.21")
}

ksp {
    arg(
        "arrowGen.include",
        listOf(
            "io.github.arrow.gen.example.topLevelFunction",
            "io.github.arrow.gen.example.genericTopLevelFunction",
            "io.github.arrow.gen.example.ContainingClass.memberFunction",
            "io.github.arrow.gen.example.ContainingClass.genericMemberFunction",
            "io.github.arrow.gen.example.GenericContainingClass.memberFunction",
            "io.github.arrow.gen.example.GenericContainingClass.memberFunctionUsingClassTypeParameter",
            "io.github.arrow.gen.example.GenericContainingClass.genericMemberFunction",
            "io.github.arrow.gen.example.GenericContainingClass.genericMemberFunctionUsingClassTypeParameter",
        ).joinToString(","),
    )
    arg("arrowGen.raise", "true")
    arg("arrowGen.either", "true")
    arg("arrowGen.effect", "true")
}

arrowGen {
    include.addAll(
        "io.github.arrow.gen.example.topLevelFunction",
        "io.github.arrow.gen.example.genericTopLevelFunction",
        "io.github.arrow.gen.example.ContainingClass.memberFunction",
        "io.github.arrow.gen.example.ContainingClass.genericMemberFunction",
        "io.github.arrow.gen.example.GenericContainingClass.memberFunction",
        "io.github.arrow.gen.example.GenericContainingClass.memberFunctionUsingClassTypeParameter",
        "io.github.arrow.gen.example.GenericContainingClass.genericMemberFunction",
        "io.github.arrow.gen.example.GenericContainingClass.genericMemberFunctionUsingClassTypeParameter",
    )
}
