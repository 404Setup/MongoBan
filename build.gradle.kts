import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    java
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "one.tranic"
version = "24.12.0"

repositories {
    maven("https://maven-central-asia.storage-download.googleapis.com/maven2/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    compileOnly("com.google.guava:guava:33.3.0-jre")
    implementation("org.mongodb:mongodb-driver-sync:5.2.1")
    implementation("redis.clients:jedis:5.2.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
}

tasks.shadowJar {
    relocate("org.simpleyaml", "one.tranic.mongoban.libs.simpleyaml");
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

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.configure { java.srcDir(generateTemplates.map { it.outputs }) }
rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)
rootProject.eclipse.synchronizationTasks(generateTemplates)