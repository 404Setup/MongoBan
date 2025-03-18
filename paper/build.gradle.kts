plugins {
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

repositories {
}

dependencies {
    implementation(project(":api"))

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    implementation("one.tranic:t-bukkit:1.0.1") {
        exclude("net.kyori", "adventure-api")
        exclude("net.kyori", "adventure-text-minimessage")
        exclude("net.kyori", "adventure-platform-bukkit")
        exclude("org.spigotmc", "spigot-api")
    }
    implementation("one.tranic:t-paper:1.0.1")
}

val libPackage = "one.tranic.mongoban.libs"

tasks.shadowJar {
    exclude("org/bson/codecs/pojo/**")

    relocate("one.tranic.t", "${libPackage}.tlib")
    relocate("one.tranic.irs", "${libPackage}.irs")
    relocate("com.mongodb", "${libPackage}.mongodb")
    relocate("redis.clients.jedis", "${libPackage}.jedis")
    relocate("org.apache.commons.pool2", "${libPackage}.pool2")
    relocate("org.json", "${libPackage}.json")
    relocate("com.amihaiemil.eoyaml", "${libPackage}.eoyaml")
    relocate("javax.json", "${libPackage}.jxjson")
    relocate("dev.nipafx.args", "${libPackage}.args")
    //relocate("org.bson", "${libPackage}.bson")

    minimize {
        exclude("META-INF/**")
        exclude("com/sun/jna/**")
        exclude("com/google/gson/**")
        exclude("com/google/errorprone/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/checkerframework/**")
        exclude("net/kyori/**")
        exclude("org/slf4j/**")
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("plugin.yml", "bungee.yml")) {
        expand(props)
    }
}