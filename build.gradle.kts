// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("org.jetbrains.kotlin:kotlin-annotation-processing-gradle:1.9.22")
    }
}

// Configure Java home via Gradle properties if not set in environment
if (System.getenv("JAVA_HOME") == null) {
    System.setProperty("org.gradle.java.home", "${System.getProperty("user.home")}/.jdks/corretto-17.0.9")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}