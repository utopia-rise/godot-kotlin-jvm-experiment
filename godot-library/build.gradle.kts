plugins {
    kotlin("multiplatform")
}

repositories {
    google()
    jcenter()
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.google.protobuf:protobuf-java:3.13.0")
            }
        }
    }
}