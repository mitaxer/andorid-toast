plugins {
    id("com.android.application")
}

android {
    namespace = "com.mitaxer.toast.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mitaxer.toast.sample"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
//    implementation(project(":library"))
    api("com.github.mitaxer:android-toast:1.0.10")
}
