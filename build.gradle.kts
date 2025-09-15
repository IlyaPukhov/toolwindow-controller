plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "com.puhovin.intellijplugin"
version = "1.3.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2023.2")
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    patchPluginXml {
        pluginName = "ToolWindow Controller"
        sinceBuild = "232"
        untilBuild = ""
    }

    signPlugin {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        token = System.getenv("PUBLISH_TOKEN")
    }

    wrapper {
        distributionType = Wrapper.DistributionType.BIN
        gradleVersion = "9.0.0"
    }

    register("printVersion") {
        val versionProvider = providers.provider { project.version }
        doLast {
            println(versionProvider.get())
        }
    }
}