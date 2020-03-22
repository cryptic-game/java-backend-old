plugins {
    application
    `java-library`
}

dependencies {
    api(project(":java-base"))
    api(project(":java-data"))
    implementation(project(":java-daemon-endpoints"))
}

application {
    applicationName = "java-daemon"
    mainClassName = "net.cryptic_game.backend.daemon.App"
}

tasks {
    jar {
        manifest {
            attributes("Implementation-Version" to project.version.toString())
        }
    }
}
