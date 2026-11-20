import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
}

val mavenVersion: String = findProperty("mavenVersion") as? String
    ?: findProperty("MAVEN_VERSION") as? String
    ?: "0.2.0"
val groupId: String = findProperty("groupId") as? String
    ?: findProperty("GROUP_ID") as? String
    ?: "io.github.sofarnobug"

group = groupId
version = mavenVersion

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    js(IR) {
        browser()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.compilations.getByName("main") {
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
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val jsMain by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val iosMain by creating {
            dependsOn(nativeMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit:1.2.1")
                implementation("androidx.test:runner:1.6.2")
            }
        }
    }
}

android {
    namespace = "com.jlj.kmp.draco"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }
    buildToolsVersion = "36.0.0"
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets.getByName("main").jniLibs.srcDir("src/androidMain/jniLibs")
}

mavenPublishing {
    coordinates(groupId, "kmp-draco", project.version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    if (findProperty("skipSign") != "true" && System.getenv("SKIP_SIGN") != "1") {
        signAllPublications()
    }
    pom {
        name.set("kmp-draco")
        description.set("Draco bitstream decoder (Kotlin face; Rust draco-core)")
        url.set("https://github.com/SoFarNoBug/kmp-draco")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("sofarnobug")
                name.set("SoFarNoBug")
            }
        }
        scm {
            url.set("https://github.com/SoFarNoBug/kmp-draco")
        }
    }
}
