plugins {
    java
    application
    id("io.freefair.aggregate-javadoc") version "5.0.0-rc2"
//    kotlin("jvm") version "1.3.70"
}

group = "net.cryptic-game"
version = "0.3.0"

application {
    mainClassName = ""
}

subprojects {
    apply(plugin = "java")
//    apply(plugin = "kotlin")

    version = project.parent!!.version

    repositories {
        mavenCentral()
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

//    tasks {
//        compileKotlin {
//            kotlinOptions.jvmTarget = "11"
//        }
//
//        compileTestKotlin {
//            kotlinOptions.jvmTarget = "11"
//        }
//    }
}

tasks {
    val cleanDocs = task("cleanDocs", Delete::class) {
        delete("$projectDir/javadoc/latest/")
    }

    task("createDocs", Copy::class) {
        dependsOn(cleanDocs)
        into("$projectDir/javadoc/latest/")
    }

    task("version") {
        println("Version $version")
    }
}

//task linkSentry(type: Exec) {
//    commandLine "sentry-cli", "releases", "new", "-p", "${System.env.SENTRY_PROJECT}", "${version.toString()}"
//    commandLine "sentry-cli", "releases", "set-commits", "--auto", "${version.toString()}"
//    commandLine "sentry-cli", "releases", "finalize", "${version.toString()}"
//    commandLine "sentry-cli", "releases", "deploys", "${version.toString()}", "new", "-e", "${System.env.SENTRY_DEPLOY_ENVIRONMENT}"
//}
