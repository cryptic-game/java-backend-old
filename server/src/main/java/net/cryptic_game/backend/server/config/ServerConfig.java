package net.cryptic_game.backend.server.config;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.DefaultConfig;

public enum ServerConfig implements DefaultConfig {

    // Ignore
    CONFIG,

    // Values
    DAEMON_HOST,
    DAEMON_PORT,

    WEBSOCKET_HOST,
    WEBSOCKET_PORT,

    HTTP_HOST,
    HTTP_PORT;

    @Override
    public void iniConfig(final Config config) {
        config.set(ServerConfig.DAEMON_HOST, "0.0.0.0");
        config.set(ServerConfig.DAEMON_PORT, 4201);

        config.set(ServerConfig.WEBSOCKET_HOST, "0.0.0.0");
        config.set(ServerConfig.WEBSOCKET_PORT, 80);

        config.set(ServerConfig.HTTP_HOST, "0.0.0.0");
        config.set(ServerConfig.HTTP_PORT, 8080);
    }
}
