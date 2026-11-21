plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "dev.stonecutterimports" // placeholder: rename to your own
version = "0.1.0"

repositories { mavenCentral() }

tasks.withType<JavaCompile>().configureEach { options.release.set(17) }

gradlePlugin {
    plugins {
        register("stonecutterImports") {
            id = "dev.stonecutterimports"
            implementationClass = "dev.stonecutterimports.ImportsPlugin"
            displayName = "Stonecutter Imports"
            description = "Per-version import management for Stonecutter projects"
        }
    }
}

// Plain-main checks, no JUnit needed. Also runs as part of `gradle check`.
val selfCheck by tasks.registering(JavaExec::class) {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("dev.stonecutterimports.SelfCheck")
}
tasks.check { dependsOn(selfCheck) }
