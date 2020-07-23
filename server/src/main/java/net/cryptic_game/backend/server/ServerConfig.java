package net.cryptic_game.backend.server;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.ConfigValue;

@Config("server")
@Data
@Setter(AccessLevel.NONE)
public class ServerConfig {

    @ConfigValue("http_host")
    private String httpHost = "0.0.0.0";

    @ConfigValue("http_port")
    private int httpPort = 80;

    @ConfigValue("java_daemon_address")
    private String javaDaemonAddress = "http://127.0.0.1:8080";

    @ConfigValue("websocket_address")
    private String websocketAddress = "127.0.0.1:80/ws";
}
