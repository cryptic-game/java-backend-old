package net.cryptic_game.backend.admin;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
public class AdminPanelConfig {

    @Value("${HTTP_HOST:0.0.0.0}")
    private String httpHost;

    @Value("${HTTP_PORT:8080}")
    private int httpPort;
}
