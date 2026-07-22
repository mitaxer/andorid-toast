plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

android {
    namespace = "com.mitaxer.common.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mitaxer.common.sample"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":library"))
}
