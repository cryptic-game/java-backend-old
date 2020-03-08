package net.cryptic_game.backend.base.config

import net.cryptic_game.backend.base.sql.SQLServerType


enum class BaseConfig : DefaultConfig {
    // Ignore
    @Suppress("unused")
    CONFIG,
    SQL_SERVER_HOSTNAME, SQL_SERVER_PORT, SQL_SERVER_TYPE, SQL_SERVER_USERNAME, SQL_SERVER_PASSWORD, SQL_SERVER_DATABASE, STORAGE_LOCATION, PRODUCTIVE, SESSION_EXPIRE, RESPONSE_TIMEOUT, LOG_LEVEL, SENTRY_DSN;

    override fun iniConfig(config: Config) {
        config[SQL_SERVER_HOSTNAME] = "0.0.0.0"
        config[SQL_SERVER_PORT] = "3306"
        config[SQL_SERVER_TYPE] = SQLServerType.MYSQL_8_0.name
        config[SQL_SERVER_USERNAME] = "cryptic"
        config[SQL_SERVER_PASSWORD] = "cryptic"
        config[SQL_SERVER_DATABASE] = "cryptic"
        config[STORAGE_LOCATION] = "/data"
        config[PRODUCTIVE] = true
        config[SESSION_EXPIRE] = 14 // days
        config[RESPONSE_TIMEOUT] = 20 // 20 seconds
        config[LOG_LEVEL] = "WARN"
        config[SENTRY_DSN] = ""
    }
}