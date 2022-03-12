plugins {
    id("org.springframework.boot") version Dependencies.springBootVersion
    kotlin("jvm") version Dependencies.kotlinVersion
    kotlin("plugin.spring") version Dependencies.kotlinVersion
    kotlin("plugin.jpa") version Dependencies.kotlinVersion
}

apply(plugin = "org.springframework.boot")
apply(plugin = "kotlin-spring")
apply(plugin = "kotlin-jpa")

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(Dependencies.hibernateReactive)
    implementation(Dependencies.coroutineJdk8)
    implementation(Dependencies.coroutineReactor)
    implementation(Dependencies.mutiny)

    implementation(Dependencies.springBootWebflux)
    implementation(Dependencies.springBootJpa)
    implementation(Dependencies.jacksonKotlinModule)
    implementation(Dependencies.h2)
    implementation(platform(Dependencies.springBootBom))

    implementation(Dependencies.agroalPool)
    implementation(Dependencies.vertxJdbcClient)
    implementation(Dependencies.junit)

    testImplementation(Dependencies.springBootTest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}