package net.cryptic_game.backend.daemon.redis;

import net.cryptic_game.backend.base.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Config
public class RedisConfiguration {

    @Bean
    StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
