package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.Bootstrap;
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
    ApiAuthenticationProvider authenticationProvider(final Bootstrap bootstrap, final BaseConfig baseConfig) {
        if (baseConfig.getApiToken().isBlank()) {
            log.warn("No Api token was specified, endpoints can be accessed without authentication.");

            if (!bootstrap.isDebug()) {
                log.error("The Daemon cannot be started in production mode without a api token.");
                bootstrap.shutdown();
            }
        }
        return new DaemonAuthenticationProvider(baseConfig.getApiToken());
    }
}
