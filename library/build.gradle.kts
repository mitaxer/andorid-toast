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

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.mitaxer"
            artifactId = "andorid-toast"
            version = System.getenv("VERSION") ?: "1.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
