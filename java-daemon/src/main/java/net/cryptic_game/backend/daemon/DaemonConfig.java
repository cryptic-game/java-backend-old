package net.cryptic_game.backend.daemon;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.ConfigValue;

@Config("daemon")
@Data
@Setter(AccessLevel.NONE)
public class DaemonConfig {

    @ConfigValue("http_host")
    private String httpHost = "0.0.0.0";

    @ConfigValue("http_port")
    private int httpPort = 80;
}
