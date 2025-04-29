import org.gradle.api.tasks.testing.logging.TestLogEvent

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
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("passed", "failed")
}
