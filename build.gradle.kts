plugins {
    kotlin("jvm") version "2.2.21"
    application
}

repositories {
    mavenCentral()
}

// Kotlin 2.2 пока не умеет target 25, поэтому собираем байткод 21 (запускается на JDK 25)
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// Какую задачу запускать: ./gradlew run -Ptask=05  (по умолчанию task01)
val task = (findProperty("task") as String?)?.padStart(2, '0') ?: "01"

application {
    mainClass.set("task$task.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
