buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

plugins {
    kotlin("jvm") version "1.4.10"
}

allprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "openrsx"
    version = "0.0.1"

    java.sourceCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
    }

    dependencies {
        // network
        implementation(group = "io.netty", name = "netty", version = "3.6.6.Final")
        implementation(group = "org.postgresql", name = "postgresql", version = "42.2.23")

        // utility
        implementation(group = "com.google.code.gson", name = "gson", version = "2.8.2")
        implementation(group = "com.google.guava", name = "guava", version = "30.1.1-jre")
        implementation(group = "it.unimi.dsi", name = "fastutil", version = "8.2.1")

        // performance
        implementation(group = "io.github.classgraph", name = "classgraph", version = "4.8.78")

        // functionality
        compileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testCompileOnly("org.projectlombok:lombok:1.18.20")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

        // logging
        runtimeOnly(group = "org.tinylog", name = "tinylog-impl", version = "2.4.0-M1")
        implementation(group = "org.tinylog", name = "tinylog-api", version = "2.4.0-M1")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

}

/*
	A list of all Source-built folders, list is  self-explanitory
*/
sourceSets {
    main {
        java {
            srcDirs("fileserver")
            srcDirs("network")
            srcDirs("combat")
            srcDirs("skills")
            srcDirs("plugins")
            srcDirs("mysql")
            srcDirs("src")
        }
    }
}