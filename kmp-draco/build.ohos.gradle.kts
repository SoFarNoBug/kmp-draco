plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    ohosArm64 {
        compilations.getByName("main") {
            cinterops {
                create("kmp_draco_native") {
                    defFile(project.file("src/nativeInterop/cinterop/kmp_draco_native.def"))
                    includeDirs(project.file("../kmp-draco-native/include"))
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val ohosArm64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

android {
    namespace = "com.jlj.kmp.draco"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
