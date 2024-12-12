plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    implementation("com.opencsv:opencsv:5.9")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    testImplementation("org.mockito:mockito-core:5.14.2")
    mockitoAgent("org.mockito:mockito-core:5.14.2") { isTransitive = false }
}

application {
    mainClass.set("src/main/java/org/example/Main.java")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}