plugins {
    id("java")
}

group = "de.erethon.papyrus"
version = "1.0"

repositories {
    mavenLocal();
}

dependencies {
    compileOnly(project(":papyrus-server"))
    compileOnly(project(":papyrus-api"))
}