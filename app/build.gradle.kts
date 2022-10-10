import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

project.afterEvaluate {
    tasks.koverHtmlReport {
        group = "kover"
        description = "Generated test coverage"
        
        dependsOn("testDebugUnitTest")

        isEnabled = true
    }

    detekt {
        toolVersion = "1.21.0"
        basePath = "$rootDir"
        config = files("$rootDir/config/detekt/detekt_config.yml")
        buildUponDefaultConfig = true
    }

    tasks.detekt.configure {
        reports {
            html.required.set(true)
            sarif.required.set(true)
            html.outputLocation.set(file("$rootDir/reports/detekt.html"))
            sarif.outputLocation.set(file("$rootDir/reports/detekt.sarif"))
        }
    }
}

kover {
    htmlReport {
        reportDir.set(layout.buildDirectory.dir("kover_report/html_result"))
        filters {
            classes {
                includes += listOf("com.minhdtm.example.weapose.*")

                excludes += listOf(
                    "*Screen*",
                    "*_Factory*",
                    "*_HiltModules*",
                    "*di*",
                    "*_Impl*",
                    "*BuildConfig*",
                    "*Activity*",
                    "*App*",
                    "*Drawer*",
                    "*Graph*",
                    "*.theme*",
                )
            }
        }
    }
}

android {
    namespace = "com.minhdtm.example.weapose"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.minhdtm.example.weapose"
        minSdk = 21
        targetSdk = 33
        versionCode = 5
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val dateTime = SimpleDateFormat("yyMMdd").format(Date().time)
        setProperty("archivesBaseName", "weapose-${versionName}-${dateTime}")

        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "retrofit2.pro", "gson.pro")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            add("-opt-in=kotlin.RequiresOptIn")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.1"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.activity:activity-compose:1.5.1")

    // Compose ui
    implementation("androidx.compose.ui:ui:1.3.0-beta02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0-beta02")

    // Material
    implementation("androidx.compose.material3:material3:1.0.0-beta02")
    implementation("androidx.compose.material:material:1.3.0-beta02")

    // Work manager
    implementation("androidx.work:work-runtime-ktx:2.8.0-alpha04")

    // Google accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.26.4-beta")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.26.5-rc")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.26.5-rc")
    implementation("com.google.accompanist:accompanist-permissions:0.26.4-beta")
    implementation("com.google.accompanist:accompanist-flowlayout:0.26.4-beta")

    // Google play services
    implementation("com.google.android.gms:play-services-location:20.0.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.libraries.places:places:2.6.0")
    implementation("com.google.maps.android:maps-compose:2.7.2")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Room
    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")

    // Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.hilt:hilt-work:1.0.0")
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0-alpha02")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0-alpha02")

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.3.0-beta02")

    // Gson
    implementation("com.google.code.gson:gson:2.9.1")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Kotlin reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:5.2.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // MockK
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0-beta02")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.0-beta02")
}
