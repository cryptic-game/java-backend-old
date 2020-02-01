package net.cryptic_game.backend.server.config;

import net.cryptic_game.backend.base.interfaces.DefaultConfig;

import java.util.HashMap;
import java.util.Map;

public enum ServerDefaultConfig implements DefaultConfig {

    WEBSOCKET_HOST("0.0.0.0"),
    WEBSOCKET_PORT(80),
    MSSOCKET_HOST("127.0.0.1"),
    MSSOCKET_PORT(1239),
    HTTP_HOST("0.0.0.0"),
    HTTP_PORT(8080),
    AUTH_ENABLED(true),
    STORAGE_LOCATION("data/"),
    MYSQL_HOSTNAME("192.168.170.130"),
    MYSQL_USERNAME("cryptic"),
    MYSQL_PASSWORD("cryptic"),
    MYSQL_DATABASE("cryptic"),
    MYSQL_PORT(3306),
    PRODUCTIVE(true),
    SESSION_EXPIRE(60 * 60 * 24 * 2), // 2 days
    RESPONSE_TIMEOUT(20), // 20 seconds
    LOG_LEVEL("INFO"),
    SENTRY_DSN("");

    private Object value;

    ServerDefaultConfig(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Map<String, String> getDefaults() {
        Map<String, String> defaults = new HashMap<>();

        for (ServerDefaultConfig e : ServerDefaultConfig.values()) {
            defaults.put(e.toString(), e.getValue().toString());
        }

        return defaults;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
