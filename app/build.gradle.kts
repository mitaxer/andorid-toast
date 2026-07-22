plugins {
    id("com.android.application")
}

android {
    namespace = "com.mitaxer.toast.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mitaxer.toast.sample"
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
