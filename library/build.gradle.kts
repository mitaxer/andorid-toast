plugins {
    id("com.android.library")
    id("maven-publish")
}

repositories {
    google()
    mavenCentral()
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

    // 明确注册 release 组件给 maven-publish 使用
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.mitaxer"
                artifactId = "andorid-toast"
                version = System.getenv("VERSION") ?: "1.0.3"
            }
        }
    }
}
