plugins {
    java
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("net.nemerosa.versioning") version "3.0.0"
}

group = "fe.aegissync"
version = versioning.info.tag ?: versioning.info.full

repositories {
    mavenLocal()
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.40.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0-RC")

    implementation("com.gitlab.grrfe:httpkt:13.0.0-alpha.59")
    implementation("com.gitlab.grrfe:kotlin-ext:0.0.19")
    implementation("com.gitlab.grrfe:koin-helper:4.0.1")
    implementation("com.gitlab.grrfe:koin-exposed:3.0.3")
    implementation("com.gitlab.grrfe:kotlinx-cli-ext:0.0.2")
    implementation("com.gitlab.grrfe:Logger:3.0.0")
    implementation("com.github.1fexd:mimetypekt:0.0.6")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5")
    implementation("io.javalin:javalin:5.6.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("org.postgresql:postgresql:42.2.27")

    implementation("at.favre.lib:hkdf:2.0.0")
    implementation("io.insert-koin:koin-core:3.2.2")

    implementation("org.xerial:sqlite-jdbc:3.39.3.0")
    testImplementation("io.insert-koin:koin-test:3.2.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    manifest {
        this.attributes["Main-Class"] = "fe.aegissync.MainKt"
    }

    this.from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    this.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
