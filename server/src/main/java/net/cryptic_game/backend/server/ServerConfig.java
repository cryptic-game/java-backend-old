package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.ConfigValue;

@Config("server")
public class ServerConfig {

    @ConfigValue("websocket_host")
    private String websocketHost = "localhost";

    @ConfigValue("websocket_port")
    private int websocketPort = 80;

    @ConfigValue("http_host")
    private String httpHost = "localhost";

    @ConfigValue("http_port")
    private int httpPort = 8080;

    public String getWebsocketHost() {
        return this.websocketHost;
    }

    public void setWebsocketHost(final String websocketHost) {
        this.websocketHost = websocketHost;
    }

    public int getWebsocketPort() {
        return this.websocketPort;
    }

    public void setWebsocketPort(final int websocketPort) {
        this.websocketPort = websocketPort;
    }

    public String getHttpHost() {
        return this.httpHost;
    }

    public void setHttpHost(final String httpHost) {
        this.httpHost = httpHost;
    }

    public int getHttpPort() {
        return this.httpPort;
    }

    public void setHttpPort(final int httpPort) {
        this.httpPort = httpPort;
    }
}
