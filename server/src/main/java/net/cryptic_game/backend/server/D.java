package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.BaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class D {

    @Bean
    ApiAuthenticationProvider authenticationProvider(final BaseConfig baseConfig) {
        return new ServerAuthenticationProvider(baseConfig.getApiToken());
    }
}
