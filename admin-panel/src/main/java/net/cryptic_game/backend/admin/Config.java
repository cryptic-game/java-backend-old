package net.cryptic_game.backend.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cryptic.admin-panel")
public class Config {

    private String apiToken;
    private String serverUrl;
    private String cookieDomain;
}
