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

    @ConfigValue("websocket_host")
    private String websocketHost = "0.0.0.0";

    @ConfigValue("websocket_port")
    private int websocketPort = 80;

    @ConfigValue("http_host")
    private String httpHost = "0.0.0.0";

    @ConfigValue("http_port")
    private int httpPort = 8080;

    @ConfigValue("playground_address")
    private String playgroundAddress = "127.0.0.1";
}
