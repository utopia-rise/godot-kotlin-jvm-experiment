import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

//TODO: this needs to be properly configured! This is just a basic setup to be able to implement the annotations
kotlin {
    macosX64("macos")
    linuxX64("linux")
    mingwX64("windows")

    targets.withType<KotlinNativeTarget> {
        compilations.getByName("main") {
            dependencies {
                implementation(project(":jni-kt2"))
            }

            defaultSourceSet {
                kotlin.srcDirs("src/nativeMain/kotlin")
            }
            val javaHome = System.getProperty("java.home")
            cinterops {
                val gdnative by creating {
                    includeDirs("$rootDir/godot_headers/")
                }
            }

            binaries {
                sharedLib(listOf(DEBUG)) {
                    linkerOpts("-L$javaHome/lib/server", "-ljvm")
                }
            }
        }
    }
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }
}

tasks {
    build {
        finalizedBy(publishToMavenLocal)
    }

    // workaround to upload gradle metadata file
    // https://github.com/bintray/gradle-bintray-plugin/issues/229
    withType<BintrayUploadTask> {
        doFirst {
            publishing.publications.withType<MavenPublication> {
                buildDir.resolve("publications/$name/module.json").also {
                    if (it.exists()) {
                        artifact(object: FileBasedMavenArtifact(it) {
                            override fun getDefaultExtension() = "module"
                        })
                    }
                }
            }
        }
    }
}