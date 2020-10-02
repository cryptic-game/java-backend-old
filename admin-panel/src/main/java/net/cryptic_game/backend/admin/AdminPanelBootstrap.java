package net.cryptic_game.backend.admin;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Key;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "net.cryptic_game.backend.admin.data.sql")
public class AdminPanelBootstrap {

    @Bean
    ApiAuthenticationProvider authenticationProvider(final Key key) {
        return new AdminPanelAuthenticationProvider(key, Groups.values());
    }
}
