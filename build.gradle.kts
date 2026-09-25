plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.puhovin.intellijplugin"
version = "1.4.0"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        val idePath = providers.gradleProperty("idePath")
        if (idePath.isPresent) local(idePath.get()) else create("IU", "2026.2.3")
        pluginVerifier()
        // JUnit 5 fixtures start the test application through TestProjectManager from the base framework.
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.JUnit5)
    }
    // kotlin-test-junit5 pins JUnit 5.10; the platform's JUnit 5 fixtures need the 5.14 API they are built against.
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Runtime only: the platform's JUnit 5 environment installs TestLoggerFactory, which references JUnit 4 classes.
    testRuntimeOnly("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(25)
    compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25 }
}

// The extension flag also skips prepareJarSearchableOptions; disabling only the task breaks a clean buildPlugin.
intellijPlatform { buildSearchableOptions = false }

tasks {
    verifyPlugin { ides.setFrom(intellijPlatform.platformPath) }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed", "skipped")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    patchPluginXml {
        pluginName = "ToolWindow Controller"
        sinceBuild = "262"
        untilBuild = provider { null }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.BIN
        gradleVersion = "9.7.1"
    }

    register("printVersion") {
        description = "Prints the plugin version"
        val pluginVersion = providers.provider { project.version.toString() }
        doLast { println(pluginVersion.get()) }
    }
}
