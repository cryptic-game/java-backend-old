package net.cryptic_game.backend.server.config

import net.cryptic_game.backend.base.config.Config
import net.cryptic_game.backend.base.config.DefaultConfig

enum class ServerConfig : DefaultConfig {
    // Ignore
    CONFIG,  // Values
    WEBSOCKET_HOST, WEBSOCKET_PORT, HTTP_HOST, HTTP_PORT;

    override fun iniConfig(config: Config) {
        config[WEBSOCKET_HOST] = "0.0.0.0"
        config[WEBSOCKET_PORT] = 80
        config[HTTP_HOST] = "0.0.0.0"
        config[HTTP_PORT] = 8080
    }
}
