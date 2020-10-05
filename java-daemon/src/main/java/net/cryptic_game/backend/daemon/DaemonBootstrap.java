package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DaemonBootstrap {

    public DaemonBootstrap(final DaemonConfig daemonConfig) {
        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());
    }

    @Bean
    ApiAuthenticationProvider authenticationProvider(final BaseConfig baseConfig) {
        return new DaemonAuthenticationProvider(baseConfig.getApiToken());
    }
}
