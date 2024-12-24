import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    java
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "one.tranic"
version = "24.12.0"

repositories {
    maven("https://maven-central-asia.storage-download.googleapis.com/maven2/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    compileOnly("net.md-5:bungeecord-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    compileOnly("net.kyori:adventure-platform-bungeecord:4.3.4")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")

    implementation("one.tranic:irs:1.3.2")

    compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")

    compileOnly("com.google.guava:guava:33.3.0-jre")
    implementation("org.mongodb:mongodb-driver-sync:5.2.1")
    implementation("redis.clients:jedis:5.2.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
}

tasks.shadowJar {
    relocate("one.tranic.irs", "one.tranic.mongoban.libs.irs")
    relocate("com.github.benmanes.caffeine.cache", "one.tranic.mongoban.libs.caffeine")
    relocate("com.mongodb", "one.tranic.mongoban.libs.mongodb")
    relocate("redis.clients.jedis", "one.tranic.mongoban.libs.jedis")
    relocate("org.apache.commons.pool2", "one.tranic.mongoban.libs.pool2")
    relocate("org.bson", "one.tranic.mongoban.libs.bson")
    relocate("org.json", "one.tranic.mongoban.libs.json")
    relocate("org.simpleyaml", "one.tranic.mongoban.libs.simpleyaml")

    minimize {
        exclude("META-INF/**")
        exclude("com/google/gson/**")
        exclude("com/google/errorprone/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/checkerframework/**")
        exclude("org/slf4j/**")
    }
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
    filesMatching(listOf("plugin.yml", "bungee.yml")) {
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