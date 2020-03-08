plugins {
    java
    application
}

dependencies {
    implementation(project(":java-base"))
    implementation(project(":java-backend"))
    implementation(project(":java-backend-impl"))
    implementation(project(":java-data"))
}

application {
    applicationName = "server"
    mainClassName = "net.cryptic_game.backend.server.AppKt"
}

tasks {
    jar {
        manifest {
            attributes("Implementation-Version" to project.version.toString())
        }
    }
}
