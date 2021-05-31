package net.cryptic_game.backend.admin;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "cryptic.admin-panel")
public class Config {

    private String apiToken;
    private String serverUrl;

}
