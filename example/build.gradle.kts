plugins {
    kotlin("jvm") version "2.1.21"
    id("io.github.fpiechowski.arrowgen") version "0.0.1"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.arrow-kt:arrow-core:2.1.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.1.2")

    ksp(project(":processor"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.21")
}

arrowGen {
    raise.set(true)
    either.set(true)
    effect.set(true)
    include.addAll(
        "io.github.fpiechowski.arrowgen.example.topLevelFunction",
        "io.github.fpiechowski.arrowgen.example.genericTopLevelFunction",
        "io.github.fpiechowski.arrowgen.example.genericTopLevelFunctionWithInnerTypeParameter",
        "io.github.fpiechowski.arrowgen.example.ContainingClass.memberFunction",
        "io.github.fpiechowski.arrowgen.example.ContainingClass.genericMemberFunction",
        "io.github.fpiechowski.arrowgen.example.GenericContainingClass.memberFunction",
        "io.github.fpiechowski.arrowgen.example.GenericContainingClass.memberFunctionUsingClassTypeParameter",
        "io.github.fpiechowski.arrowgen.example.GenericContainingClass.genericMemberFunction",
        "io.github.fpiechowski.arrowgen.example.GenericContainingClass.genericMemberFunctionUsingClassTypeParameter",
        "io.github.fpiechowski.arrowgen.example.GenericContainingClass.memberFunctionWithInnerTypeParameter",
    )
    exclude.add("io.github.fpiechowski.arrowgen.example.excludedTopLevelFunction")
}
