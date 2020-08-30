package net.cryptic_game.backend.daemon;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.config.Config;

@Config("daemon")
@Data
@Setter(AccessLevel.NONE)
public class DaemonConfig {

    private String httpHost = "0.0.0.0";

    private int httpPort = 8080;

    private String serverUrl = "http://127.0.0.1:80/api";
}
