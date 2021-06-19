package net.cryptic_game.backend.server.api.handler.websocket;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
final class WebsocketApiConfig {

    @Value("${WEBSOCKET_API_PATH:ws}")
    private String path;
}
