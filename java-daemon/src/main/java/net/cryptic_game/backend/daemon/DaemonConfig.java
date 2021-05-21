package net.cryptic_game.backend.daemon;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
class DaemonConfig {

    @Value("${SERVER_ADDRESS:http://127.0.0.1:8080/api}")
    private String serverAddress;
}
