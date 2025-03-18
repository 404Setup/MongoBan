plugins {
    java
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

allprojects {
    group = "one.tranic"
    version = "25.03.0"

    apply(plugin = "java")

    repositories {
        maven("https://maven-central-asia.storage-download.googleapis.com/maven2/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
        maven("https://repo.opencollab.dev/main/")
    }

    dependencies {
        implementation("one.tranic:t-base:1.2.6")
        implementation("one.tranic:t-utils:1.2.2.1")
        implementation("one.tranic:t-network:1.0.0")

        compileOnly("net.kyori:adventure-api:4.19.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.19.0")
        compileOnly("net.kyori:adventure-text-serializer-legacy:4.19.0")

        compileOnly("org.jetbrains:annotations:26.0.2")
    }

    val targetJavaVersion = 21

    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}