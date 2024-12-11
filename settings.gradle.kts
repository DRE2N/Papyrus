pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "fork"

val forkApiDir = file("fork-api")
if (forkApiDir.exists()) {
    include(forkApiDir.name)
}
val forkServerDir = file("fork-server")
if (forkServerDir.exists()) {
    include(forkServerDir.name)
}
