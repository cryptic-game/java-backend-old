plugins {
    application
}

dependencies {
    implementation(project(":java-base"))
    implementation(project(":java-data"))
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
