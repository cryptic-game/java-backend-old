package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DaemonBootstrap {

    public DaemonBootstrap(final DaemonConfig daemonConfig) {
        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());
    }
}
