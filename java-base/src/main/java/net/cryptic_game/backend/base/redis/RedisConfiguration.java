package net.cryptic_game.backend.base.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "net.cryptic_game.backend.data.redis.repositories")
public class RedisConfiguration {

    /**
     * Creates a {@link RedisConnectionFactory}.
     *
     * @param redisConfig The config the generate the {@link RedisConnectionFactory} from
     * @return The {@link RedisConnectionFactory}
     */
    @Bean
    public RedisConnectionFactory connectionFactory(final RedisConfig redisConfig) {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisConfig.getHostname(), redisConfig.getPort());
        if (redisConfig.getPassword() != null && !redisConfig.getPassword().isBlank()) configuration.setPassword(redisConfig.getPassword());
        return new LettuceConnectionFactory(configuration);
    }

    /**
     * Creates a {@link RedisTemplate}.
     *
     * @param connectionFactory A {@link RedisConnectionFactory}
     * @return The {@link RedisTemplate}
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    /**
     * Creates a {@link StringRedisTemplate}.
     *
     * @param connectionFactory A {@link RedisConnectionFactory}
     * @return The {@link StringRedisTemplate}
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
