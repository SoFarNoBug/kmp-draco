/**
 * Ohos / KBA composite for Holm `includeBuild`.
 * Main settings has no ohosArm64; includeBuild this directory for Ohos.
 */
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }
    }
}

rootProject.name = "kmp-draco-ohos"

include(":kmp-draco")
project(":kmp-draco").projectDir = file("../kmp-draco")
project(":kmp-draco").buildFileName = "build.ohos.gradle.kts"
