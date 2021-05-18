plugins {
    application
    kotlin("jvm") version "1.4.20"
}

val junitVersion = "5.6.2"

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "idea")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "openrsx"
    version = "0.0.1"

    java.sourceCompatibility = JavaVersion.toVersion('8')
    java.targetCompatibility = JavaVersion.toVersion('8')

    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
        maven(url = "https://repo.maven.apache.org/maven2")
        maven(url = "https://jitpack.io")
    }
}

dependencies {
    // Jvm
    implementation(kotlin("stdlib"))

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.4.2")

    // Network
    implementation("io.netty:netty:3.10.6.Final")

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.30")

    // Utilities
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.guava:guava:29.0-jre")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.json:json:20210307")
    implementation("org.apache.commons:commons-lang3:3.10")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
