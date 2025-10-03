plugins {
    kotlin("jvm") version "1.9.23"
    application
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.mcxross:kaptos:0.2.0")
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("MainActivity")
}