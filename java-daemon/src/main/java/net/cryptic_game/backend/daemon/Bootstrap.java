package net.cryptic_game.backend.daemon;

import net.getnova.framework.core.NovaBanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication(scanBasePackages = "net.cryptic_game.backend")
public class Bootstrap {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .banner(new NovaBanner())
                .run(args);
    }
}
