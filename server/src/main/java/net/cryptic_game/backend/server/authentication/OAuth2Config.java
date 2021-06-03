package net.cryptic_game.backend.server.authentication;

import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter
public class OAuth2Config {

    @Value("${DISCORD_OAUTH2_CLIENT_ID:}")
    private String discordClientId;

    @Value("${DISCORD_OAUTH2_CLIENT_SECRET:}")
    private String discordClientSecret;

    @Value("${DISCORD_OAUTH2_REDIRECT_URI:}")
    private String discordRedirectUri;

    @Value("${DISCORD_OAUTH2_AUTH_URI:}")
    private String discordAuthUri;

    @Value("${GITHUB_OAUTH2_CLIENT_ID:}")
    private String githubClientId;

    @Value("${GITHUB_OAUTH2_CLIENT_SECRET:}")
    private String githubClientSecret;

    @Value("${GITHUB_OAUTH2_AUTH_URI:}")
    private String githubAuthUri;
}
