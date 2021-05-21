package net.cryptic_game.backend.data;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EntityScan("net.cryptic_game.backend.data")
@EnableJpaRepositories("net.cryptic_game.backend.data.sql.repositories")
@EnableRedisRepositories("net.cryptic_game.backend.data.redis.repositories")
public class DataConfiguration {
}
