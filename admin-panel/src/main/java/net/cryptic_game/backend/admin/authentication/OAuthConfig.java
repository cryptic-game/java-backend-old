package net.cryptic_game.backend.admin.authentication;

import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter
public class OAuthConfig {

    @Value("${OAUTH_CLIENT_ID:}")
    private String clientId;

    @Value("${OAUTH_CLIENT_SECRET:}")
    private String clientSecret;
}
