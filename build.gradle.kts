plugins {
    kotlin ("jvm") version "1.4.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories{
    mavenCentral()
    jcenter()
}
dependencies{
    implementation ("org.jetbrains.exposed:exposed-core:0.25.1")
    implementation ("org.jetbrains.exposed:exposed-dao:0.25.1")
    implementation ("org.jetbrains.exposed:exposed-jdbc:0.25.1")
    implementation ("org.slf4j:slf4j-nop:1.7.30")

    implementation ("mysql:mysql-connector-java:8.0.19")
    implementation ("com.zaxxer:HikariCP:3.4.2")
}
application{
    mainClassName = "MainKt"
}