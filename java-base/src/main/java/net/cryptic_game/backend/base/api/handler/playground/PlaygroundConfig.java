package net.cryptic_game.backend.base.api.handler.playground;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
public final class PlaygroundConfig {

    @Value("${PLAYGROUND_ENABLED:false}")
    private boolean enabled;

    @Value("${PLAYGROUND_PATH:playground}")
    private String path;
}
