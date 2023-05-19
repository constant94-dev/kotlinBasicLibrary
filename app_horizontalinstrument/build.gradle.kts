plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.app_horizontalinstrument"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.app_horizontalinstrument"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true // Enables code shrinking for the release build type.
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        compose = true // compose 기능이 사용 설정된다.
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3" // kotlin 버전 관리에 연결된다.
        // Project 수준의 호환되는 kotlin 버전과 compose 컴파일러 버전을 맞춰주어야 한다.
    }
}

dependencies {

    val appcompatVersion = "1.6.1"
    val activityVersion = "1.6.1"
    val lifecycleVersion = "2.5.1"
    val liveDataVersion = "1.3.3"
    val material3Version = "1.0.1"
    val materialVersion = "1.3.1"
    val navVersion = "2.5.3"
    val coreVersion = "1.9.0"
    val pagerVersion = "0.30.1"
    val coilVersion = "2.3.0"
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    // only import the main APIs for the underlying toolkit systems, such as input and measurement/layout
    implementation("androidx.compose.ui:ui")
    // Material Design 3
    //implementation("androidx.compose.material3:material3:$material3Version")
    // Material Design
    implementation("androidx.compose.material:material:$materialVersion")

    // 앱의 API 레벨 호환성을 해결한다.
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    // For loading and tinting drawables on older versions of the platform
    implementation("androidx.appcompat:appcompat-resources:$appcompatVersion")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Kotlin core
    implementation("androidx.core:core-ktx:$coreVersion")

    // Lifecycles only (without ViewModel or LiveData)
    //implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Optional - Test helpers for Lifecycle runtime
    //testImplementation ("androidx.lifecycle:lifecycle-runtime-testing:$lifecycleVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:$liveDataVersion")

    // Kotlin activity
    implementation("androidx.activity:activity-compose:$activityVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Pager
    implementation("com.google.accompanist:accompanist-pager:$pagerVersion")
    // Pager indicators
    implementation("com.google.accompanist:accompanist-pager-indicators:$pagerVersion")

    // coil
    implementation("io.coil-kt:coil-compose:$coilVersion")
}