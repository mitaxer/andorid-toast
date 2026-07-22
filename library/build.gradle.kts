plugins {
    id("com.android.library")
    id("maven-publish")
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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.mitaxer"
                artifactId = "andorid-toast"
                version = System.getenv("VERSION") ?: "1.0.0"
            }
        }
    }
}
