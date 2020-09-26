package net.cryptic_game.backend.base.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
public class JwtConfig {

    @Value("${JWT_KEY:}")
    private String key;
}
