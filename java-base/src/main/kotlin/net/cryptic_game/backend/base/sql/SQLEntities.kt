package net.cryptic_game.backend.base.sql

import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.hibernate.dialect.*
import org.hibernate.service.ServiceRegistry
import java.nio.charset.StandardCharsets
import java.sql.Driver
import java.sql.SQLException
import java.util.*
import kotlin.reflect.KClass

class SQLServer(val host: String, val port: Int, val database: String, val username: String, val password: String, val sqlServerType: SQLServerType)

/**
 * All available Drivers:
 *
 *  *
 * MariaDB
 *
 *  * mariadb_05_3: 05.3
 *  * mariadb_10_0: 10.0
 *  * mariadb_10_2: 10.2
 *  * mariadb_10_3: 10.3
 *
 *
 *  *
 * MySQL
 *
 *  * mysql_5_0: 5.0
 *  * mysql_5_5: 5.5
 *  * mysql_5_7: 5.7
 *  * mysql_8_0: 8.0
 *
 *
 *
 */
@Suppress("unused")
enum class SQLServerType(
        @get:JvmName("getName")
        val readableName: String,
        val urlPrefix: String,
        driver: KClass<out Driver>,
        dialect: KClass<out Dialect>
) {

    MARIADB_05_3("mariadb_05_3", "mariadb", org.mariadb.jdbc.Driver::class, MariaDB53Dialect::class),
    MARIADB_10_0("mariadb_10_0", "mariadb", org.mariadb.jdbc.Driver::class, MariaDB10Dialect::class),
    MARIADB_10_2("mariadb_10_2", "mariadb", org.mariadb.jdbc.Driver::class, MariaDB102Dialect::class),
    MARIADB_10_3("mariadb_10_3", "mariadb", org.mariadb.jdbc.Driver::class, MariaDB103Dialect::class),

    MYSQL_5_0("mysql_5_0", "mysql", com.mysql.cj.jdbc.Driver::class, MySQL5Dialect::class),
    MYSQL_5_5("mysql_5_5", "mysql", com.mysql.cj.jdbc.Driver::class, MySQL55Dialect::class),
    MYSQL_5_7("mysql_5_7", "mysql", com.mysql.cj.jdbc.Driver::class, MySQL57Dialect::class),
    MYSQL_8_0("mysql_8_0", "mysql", com.mysql.cj.jdbc.Driver::class, MySQL8Dialect::class),

    NONE("none", "none", Driver::class, Dialect::class);

    val driver: String = driver.simpleName ?: "Unknown"
    val dialect: String = dialect.simpleName ?: "Unknown"

    companion object {
        @JvmStatic
        fun getServer(name: String) = values().firstOrNull { it.readableName == name }
                ?: NONE
    }
}

class SQLConnection {
    private val configuration: Configuration = Configuration()
    private lateinit var factory: SessionFactory

    fun init(sqlServer: SQLServer, debug: Boolean) {
        configuration.addProperties(generateSettings(sqlServer, debug))
        val registry: ServiceRegistry = StandardServiceRegistryBuilder()
                .applySettings(configuration.properties)
                .build()
        factory = configuration.buildSessionFactory(registry)
    }

    private fun generateSettings(sqlServer: SQLServer, debug: Boolean): Properties {
        val settings = Properties()
        settings[Environment.DRIVER] = sqlServer.sqlServerType.driver
        settings[Environment.URL] = "jdbc:" + sqlServer.sqlServerType.urlPrefix + "://" + sqlServer.host  + ":" + sqlServer.port + "/" + sqlServer.database + "?autoReconnect=true&useUnicode=true&characterEncoding=utf-8"
        settings[Environment.AUTOCOMMIT] = false
        settings[Environment.USER] = sqlServer.username
        settings[Environment.PASS] = sqlServer.password
        settings[Environment.DIALECT] = sqlServer.sqlServerType.dialect
        settings[Environment.HBM2DDL_AUTO] = "update"
        settings[Environment.HBM2DDL_CHARSET_NAME] = StandardCharsets.UTF_8.toString()
        settings[Environment.CONNECTION_PREFIX + ".CharSet"] = StandardCharsets.UTF_8.toString()
        settings[Environment.CONNECTION_PREFIX + ".characterEncoding"] = StandardCharsets.UTF_8.toString()
        settings[Environment.CONNECTION_PREFIX + ".useUnicode"] = true
        settings["connection.autoReconnect"] = true
        settings["connection.autoReconnectForPools"] = true
        settings["connection.is-connection-validation-required"] = true
        settings[Environment.JDBC_TIME_ZONE] = "UTC"
        settings[Environment.C3P0_MIN_SIZE] = 2
        settings[Environment.C3P0_MAX_SIZE] = 20
        settings[Environment.C3P0_ACQUIRE_INCREMENT] = 3
        settings[Environment.C3P0_TIMEOUT] = 300
        settings[Environment.C3P0_MAX_STATEMENTS] = 50
        settings[Environment.C3P0_IDLE_TEST_PERIOD] = 300
        if (debug) settings[Environment.SHOW_SQL] = true
        return settings
    }

    @Throws(SQLException::class)
    fun addEntity(entity: Class<out TableModel?>?) {
        if (!this::factory.isInitialized) configuration.addAnnotatedClass(entity) else throw SQLException("It's too late to register any more entities.")
    }

    @Throws(HibernateException::class)
    fun openSession(): Session = factory.openSession()

    fun close() = factory.close()

}
