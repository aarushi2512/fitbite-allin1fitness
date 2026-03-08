buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version("8.2.1") apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}