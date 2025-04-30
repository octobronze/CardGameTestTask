import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ser.std.MapProperty

plugins {
    application
    id("java")
}

repositories {
    mavenCentral();
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jjwt.jackson)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.postgresql)
    compileOnly(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
    annotationProcessor(libs.lombok)
}

application {
    mainClass = "com.example.CardGame.CardGameApplication"
    val listOfEnvs = File(".env").bufferedReader().readLines()
    applicationDefaultJvmArgs += listOfEnvs.stream().map {
        "-D$it"
    }.toList()
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("passed", "failed")
}
