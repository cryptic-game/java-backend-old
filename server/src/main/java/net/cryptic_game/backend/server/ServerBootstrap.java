package net.cryptic_game.backend.server;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiService;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.websocket.WebSocketDaemonEndpoints;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan
public class ServerBootstrap {

    public ServerBootstrap(final Bootstrap bootstrap,
                           final BaseConfig baseConfig,
                           final ServerConfig config,
                           final WebsocketApiService websocketApiService) {

        final DaemonHandler daemonHandler = new DaemonHandler(websocketApiService.getEndpoints(), baseConfig.getApiToken(), bootstrap);

        try {
            daemonHandler.setSend(new WebSocketDaemonEndpoints(baseConfig.getApiToken()),
                    WebSocketDaemonEndpoints.class.getDeclaredMethod("send", /*ApiClient.class,*/ String.class, ApiEndpointData.class, JsonObject.class));
        } catch (NoSuchMethodException e) {
            log.error("Error while setting daemon endpoint handling method.", e);
        }

//        if (bootstrap.isDebug())
//            httpServerCodec.addLocationProvider("playground", new PlaygroundLocationProvider(config.getWebsocketAddress(),
//                    webSocketEndpointHandler.getApiList().getCollections().values()));

        System.getenv()
                .entrySet()
                .parallelStream()
                .filter(entry -> entry.getKey().startsWith("DAEMON_"))
                .forEach(entry -> daemonHandler.registerDaemon(entry.getKey().substring(7).replace('_', '-').toLowerCase(), entry.getValue()));
    }
}
