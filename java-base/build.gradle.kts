plugins {
    `java-library`
}

dependencies {
    // Database
    api("org.hibernate:hibernate-core:5.4.12.Final")
    api("org.hibernate:hibernate-c3p0:5.4.12.Final")
    api("mysql:mysql-connector-java:8.0.19")
    api("org.mariadb.jdbc:mariadb-java-client:2.6.0")
    api("org.postgresql:postgresql:42.2.11")

    // Logger
    api("org.apache.logging.log4j:log4j-core:2.13.1")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.13.1")

    // Network
    api("io.netty:netty-all:4.1.48.Final")

    // Json
    api("com.google.code.gson:gson:2.8.6")

    // BCrypt
    api("at.favre.lib:bcrypt:0.9.0")

    // Validator
    api("commons-validator:commons-validator:1.6")

    // Sentry
    api("io.sentry:sentry-log4j2:1.7.30")

    // Reflections
    api("org.reflections:reflections:0.9.12")
}
