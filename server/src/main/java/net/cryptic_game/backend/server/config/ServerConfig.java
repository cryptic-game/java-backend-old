package net.cryptic_game.backend.server.config;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.DefaultConfig;
import net.cryptic_game.backend.base.sql.SQLServerType;

public enum ServerConfig implements DefaultConfig {

    // Ignore
    CONFIG,

    // Values
    WEBSOCKET_HOST,
    WEBSOCKET_PORT,

    HTTP_HOST,
    HTTP_PORT,

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
    SENTRY_DSN;

    @Override
    public void iniConfig(final Config config) {
        config.set(ServerConfig.WEBSOCKET_HOST, "0.0.0.0");
        config.set(ServerConfig.WEBSOCKET_PORT, 80);

        config.set(ServerConfig.HTTP_HOST, "0.0.0.0");
        config.set(ServerConfig.HTTP_PORT, 8080);

        config.set(ServerConfig.SQL_SERVER_HOSTNAME, "0.0.0.0");
        config.set(ServerConfig.SQL_SERVER_PORT, "3306");
        config.set(ServerConfig.SQL_SERVER_TYPE, SQLServerType.MYSQL_8_0.getName());
        config.set(ServerConfig.SQL_SERVER_USERNAME, "cryptic");
        config.set(ServerConfig.SQL_SERVER_PASSWORD, "cryptic");
        config.set(ServerConfig.SQL_SERVER_DATABASE, "cryptic");

        config.set(ServerConfig.STORAGE_LOCATION, "/data");
        config.set(ServerConfig.PRODUCTIVE, true);
        config.set(ServerConfig.SESSION_EXPIRE, 60 * 60 * 24 * 2); // 2 days
        config.set(ServerConfig.RESPONSE_TIMEOUT, 20); // 20 seconds
        config.set(ServerConfig.LOG_LEVEL, "INFO");
        config.set(ServerConfig.SENTRY_DSN, "");
    }
}
