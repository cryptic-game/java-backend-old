package net.cryptic_game.backend.server;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
class ServerConfig {

    @Value("${HTTP_HOST:0.0.0.0}")
    private String httpHost;

    @Value("${HTTP_PORT:8080}")
    private int httpPort;

    @Value("${JAVA_DAEMON_ADDRESS:http://localhost:8081}")
    private String javaDaemonAddress;

    @Value("${PYTHON_DAEMON_ADDRESS:http://localhost:8082}")
    private String pythonDaemonAddress;

    @Value("${WEBSOCKET_ADDRESS:127.0.0.1:8080/ws}")
    private String websocketAddress;
}
