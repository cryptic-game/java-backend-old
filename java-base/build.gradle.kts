plugins {
    `java-library`
}

dependencies {
    // Database
    api("org.hibernate:hibernate-core:5.4.15.Final")
    api("org.hibernate:hibernate-c3p0:5.4.15.Final")
    api("mysql:mysql-connector-java:8.0.20")
    api("org.mariadb.jdbc:mariadb-java-client:2.6.0")
    api("org.postgresql:postgresql:42.2.12")
    api("com.h2database:h2:1.4.200")

    // Logger
    api("org.apache.logging.log4j:log4j-core:2.13.3")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
    api("org.fusesource.jansi:jansi:1.18")

    // Network
    api("io.netty:netty-all:4.1.50.Final")

    // Json
    api("com.google.code.gson:gson:2.8.6")

    // Yaml
    api("org.yaml:snakeyaml:1.26")

    // BCrypt
    api("at.favre.lib:bcrypt:0.9.0")

    // Sentry
    api("io.sentry:sentry-log4j2:1.7.30")

    // Reflections
    api("org.reflections:reflections:0.9.12")

    api("com.sun.activation:javax.activation:1.2.0")

}
