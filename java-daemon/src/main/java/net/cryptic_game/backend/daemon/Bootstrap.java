package net.cryptic_game.backend.daemon;

import net.cryptic_game.backend.base.CrypticBanner;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication(scanBasePackages = "net.cryptic_game.backend")
public class Bootstrap {

    public Bootstrap(final DaemonConfig daemonConfig) {
        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());
    }

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .banner(new CrypticBanner())
                .run(args);
    }
}
