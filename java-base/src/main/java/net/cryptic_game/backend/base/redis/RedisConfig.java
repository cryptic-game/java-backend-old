package net.cryptic_game.backend.base.redis;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
class RedisConfig {

    @Value("${REDIS_HOSTNAME:localhost}")
    private String hostname;

    @Value("${REDIS_PORT:6379}")
    private int port;

    @Value("${REDIS_PASSWORD:}")
    private String password;
}
