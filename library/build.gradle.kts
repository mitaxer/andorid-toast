plugins {
    id("com.android.library")
}

android {
    namespace = "com.mitaxer.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // no external dependencies
}
