package net.cryptic_game.backend.daemon;

import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication(scanBasePackages = "net.cryptic_game.backend")
public class Bootstrap {

    public Bootstrap(final DaemonConfig daemonConfig) {
        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());
    }

    public static void main(final String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
