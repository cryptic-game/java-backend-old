package net.cryptic_game.backend.base.config;

import net.cryptic_game.backend.base.sql.SQLServerType;

public enum BaseConfig implements DefaultConfig {

    // Ignore
    CONFIG,

    SQL_SERVER_HOSTNAME,
    SQL_SERVER_PORT,
    SQL_SERVER_TYPE,
    SQL_SERVER_USERNAME,
    SQL_SERVER_PASSWORD,
    SQL_SERVER_DATABASE,

    STORAGE_LOCATION,
    PRODUCTIVE,
    SESSION_EXPIRE,
    RESPONSE_TIMEOUT,
    LOG_LEVEL,
    SENTRY_DSN,

    USE_UNIX_SOCKET,
    UNIX_SOCKET_PATH;

    @Override
    public void iniConfig(final Config config) {
        config.set(BaseConfig.SQL_SERVER_HOSTNAME, "0.0.0.0");
        config.set(BaseConfig.SQL_SERVER_PORT, "3306");
        config.set(BaseConfig.SQL_SERVER_TYPE, SQLServerType.MYSQL_8_0.getName());
        config.set(BaseConfig.SQL_SERVER_USERNAME, "cryptic");
        config.set(BaseConfig.SQL_SERVER_PASSWORD, "cryptic");
        config.set(BaseConfig.SQL_SERVER_DATABASE, "cryptic");

        config.set(BaseConfig.STORAGE_LOCATION, "/data");
        config.set(BaseConfig.PRODUCTIVE, true);
        config.set(BaseConfig.SESSION_EXPIRE, 14); // days
        config.set(BaseConfig.RESPONSE_TIMEOUT, 20); // 20 seconds
        config.set(BaseConfig.LOG_LEVEL, "WARN");
        config.set(BaseConfig.SENTRY_DSN, "");

        config.set(BaseConfig.USE_UNIX_SOCKET, true);
        config.set(BaseConfig.UNIX_SOCKET_PATH, "/var/run/cryptic.sock");
    }
}
