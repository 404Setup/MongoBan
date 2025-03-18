plugins {
}

repositories {
}

dependencies {
    compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")

    compileOnly("com.google.guava:guava:33.3.0-jre")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("dev.nipafx.args:record-args:0.9.2")
    implementation("org.mongodb:mongodb-driver-sync:5.3.0")
    implementation("redis.clients:jedis:5.2.0")
    implementation("com.amihaiemil.web:eo-yaml:8.0.6")
}