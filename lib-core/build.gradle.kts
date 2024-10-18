plugins {
    java
}

group = "gc.garcol"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

var agronaVersion = "1.23.1"
var lombokVersion = "1.18.34"

dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.agrona:agrona:${agronaVersion}")
}
