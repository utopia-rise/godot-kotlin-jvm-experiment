plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.utopia-rise:godot-library:0.1.0")
}

tasks {
    shadowJar {
        archiveBaseName.set("bootstrap")
        archiveVersion.set("")
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}