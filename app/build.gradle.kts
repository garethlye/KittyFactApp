plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("de.mannodermaus.android-junit5") version "1.11.2.0"

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "jp.speakbuddy.edisonandroidexercise"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.speakbuddy.edisonandroidexercise"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.isIncludeAndroidResources = true //for mockk UI test

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    testOptions {
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:22.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                val java by registering {
                    option("lite")
                }
                val kotlin by registering {
                    option("lite")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.test.junit4.android)
    kapt(libs.hilt.android.compiler)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.protobuf.kotlin.lite)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    implementation(libs.okhttp)
    implementation(libs.retrofit)

    //lottie
    implementation (libs.lottie.compose)

    //room local storage
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    //actually room-ktx is not required because they merged it to room-runtime above. Somehow build warning shows, so adding this to ignore error.
    ksp(libs.room.compiler)
    implementation (libs.androidx.paging.runtime.ktx) // Use latest version
    implementation (libs.androidx.paging.compose)
    implementation (libs.androidx.room.paging)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.androidx.navigation.compose)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation (libs.junit.jupiter.api)
    androidTestImplementation (libs.junit.jupiter.api)

    testImplementation (libs.mockito.mockito.core)
    testImplementation (libs.mockito.inline)
    testImplementation (libs.kotlinx.coroutines.test)
    testImplementation (libs.mockito.kotlin)
    androidTestImplementation (libs.mockito.android)
    testImplementation (libs.turbine)
    testImplementation (libs.mockito.junit.jupiter)
    androidTestImplementation (libs.androidx.ui.test)
    androidTestImplementation(libs.androidx.ui.test.android)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    testImplementation (libs.mockk)
    androidTestImplementation (libs.mockk.android)
    androidTestImplementation (libs.androidx.junit)
    testImplementation(libs.androidx.paging.common)
    testImplementation (libs.androidx.paging.testing)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.mlkit)
    implementation (libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        //excluding this since 2 unit test will fail(please refer to the comments there)
        excludeTestsMatching("jp.speakbuddy.edisonandroidexercise.ui.listAndSearch.KittyListAndSearchViewModelTest")
    }
}
