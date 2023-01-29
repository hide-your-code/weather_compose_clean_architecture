import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Implemented later because we had an exception when we were trying to run tests.
//    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlinx.kover") version "0.7.0-Beta"
    id("io.gitlab.arturbosch.detekt") version "1.23.0-RC3"
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

project.afterEvaluate {
    detekt {
        toolVersion = "1.23.0-RC3"
        basePath = "$rootDir"
        config.setFrom(files("$rootDir/config/detekt/detekt_config.yml"))
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
    useKoverTool()
}

koverReport {
    androidReports("debug") {
        html {
            setReportDir(layout.buildDirectory.dir("kover_report/html_result"))

            filters {
                excludes {
                    classes(
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

                includes {
                    classes("com.minhdtm.example.weapose.*")
                }
            }
        }
    }
}

android {
    namespace = "com.minhdtm.example.weapose"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.minhdtm.example.weapose"
        minSdk = 21
        targetSdk = 34
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "retrofit2.pro",
                "gson.pro",
                "okhttp3.pro",
                "firebase-crashlytics.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            add("-opt-in=kotlin.RequiresOptIn")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Compose ui
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)

    // Material
    implementation(libs.androidx.compose.material.get2())
    implementation(libs.androidx.compose.material.get3())

    // Work manager
    implementation(libs.androidx.work)

    // Google accompanist
    implementation(libs.bundles.google.accompanist)

    // Google play services
    implementation(libs.google.play.services.location)
    implementation(libs.google.play.services.maps)
    implementation(libs.google.places)
    implementation(libs.google.maps)

    // Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.bundles.retrofit2)

    // Okhttp
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.android.compiler)

    // Navigation
    implementation(libs.androidx.navigation)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData
    implementation(libs.androidx.livedata)

    // Gson
    implementation(libs.google.gson)

    // Timber
    implementation(libs.timber)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Lottie
    implementation(libs.lottie)

    // Junit
    testImplementation(libs.junit.get4())

    // Kotlin reflect
    testImplementation(libs.kotlin.reflect)

    // Coroutines test
    testImplementation(libs.kotlinx.coroutines.test)

    // MockK
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent.jvm)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit)

    // UI Debugging
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}
