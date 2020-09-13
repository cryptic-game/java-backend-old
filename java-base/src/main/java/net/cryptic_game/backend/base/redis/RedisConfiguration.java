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

    @Bean
    public RedisConnectionFactory connectionFactory(final RedisConfig redisConfig) {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisConfig.getHostname(), redisConfig.getPort());
        if (redisConfig.getPassword() != null && !redisConfig.getPassword().isBlank()) configuration.setPassword(redisConfig.getPassword());
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
