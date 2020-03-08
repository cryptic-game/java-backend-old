plugins {
    java
    application
}

dependencies {
    implementation(project(":java-base"))
    implementation(project(":java-backend"))
    implementation(project(":java-backend-impl"))
}

application {
    applicationName = "java-daemon"
    mainClassName = "net.cryptic_game.backend.daemon.AppKt"
}

tasks {
    jar {
        manifest {
            attributes("Implementation-Version" to project.version.toString())
        }
    }
}
