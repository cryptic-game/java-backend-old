package net.cryptic_game.backend.server;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
class ServerConfig {

    @Value("${WEBSOCKET_ADDRESS:ws://127.0.0.1:8080/ws/}")
    private String websocketAddress;
}
