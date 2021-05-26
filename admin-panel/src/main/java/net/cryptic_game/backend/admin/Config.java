package net.cryptic_game.backend.admin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Config {

    @Value("${API_TOKEN:}")
    private String apiToken;

    @Value("${SERVER_URL:}")
    private String serverUrl;

}
