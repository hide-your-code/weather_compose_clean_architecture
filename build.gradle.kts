plugins {
    id("com.android.application") version "8.0.0-alpha02" apply false
    id("com.android.library") version "8.0.0-alpha02" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.22" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
