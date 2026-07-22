plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.mitaxer.toast"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 明确注册 release 组件�?maven-publish 使用
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.10.0")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.mitaxer"
                artifactId = "android-toast"
                version = System.getenv("VERSION") ?: "1.0"
            }
        }
    }
}
