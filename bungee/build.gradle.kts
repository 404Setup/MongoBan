plugins {
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

repositories {
}

dependencies {
    implementation(project(":api"))

    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-platform-bungeecord:4.3.4")

    implementation("one.tranic:t-bungee:1.0.0")
}

val libPackage = "one.tranic.mongoban.libs"

tasks.shadowJar {
    exclude("org/bson/codecs/pojo/**")

    relocate("net.kyori", "${libPackage}.kyori")
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
        exclude("org/slf4j/**")
    }
}