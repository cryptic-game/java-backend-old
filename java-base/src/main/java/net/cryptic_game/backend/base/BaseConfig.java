package net.cryptic_game.backend.base;

import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Config
public final class BaseConfig {

    @Value("${API_TOKEN}")
    private String apiToken;
}
