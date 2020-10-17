package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DaemonBootstrap {

    public DaemonBootstrap(final Bootstrap bootstrap, final DaemonConfig daemonConfig, final BaseConfig baseConfig) {
        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());

        if (baseConfig.getApiToken().isBlank()) {
            log.warn("No Api token was specified, endpoints can be accessed without authentication.");

            if (!bootstrap.isDebug()) {
                log.error("The Daemon cannot be started in production mode without a api token.");
                bootstrap.shutdown();
            }
        }
    }
}
