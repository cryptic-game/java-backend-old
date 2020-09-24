package net.cryptic_game.backend.server;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.api.netty.websocket.WebSocketLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupService;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyServer;
import net.cryptic_game.backend.base.netty.server.NettyServerService;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.http.HttpDaemonEndpoints;
import net.cryptic_game.backend.server.server.http.HttpInfoEndpoint;
import net.cryptic_game.backend.server.server.playground.PlaygroundLocationProvider;
import net.cryptic_game.backend.server.server.websocket.WebSocketDaemonEndpoints;
import net.cryptic_game.backend.server.server.websocket.WebSocketInfoEndpoints;
import net.cryptic_game.backend.server.server.websocket.WebSocketUserEndpoints;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Slf4j
@Configuration
public class ServerBootstrap {

    public ServerBootstrap(final Bootstrap bootstrap,
                           final BaseConfig baseConfig,
                           final ServerConfig config,
                           final ApplicationContext context,
                           final NettyServerService serverService,
                           final EventLoopGroupService eventLoopGroupService) {

        final ApiEndpointHandler webSocketEndpointHandler = new ApiEndpointHandler();
        final ApiEndpointHandler httpEndpointHandler = new ApiEndpointHandler();
        final DaemonHandler daemonHandler = new DaemonHandler(webSocketEndpointHandler.getApiList(), baseConfig.getApiToken());

        webSocketEndpointHandler.addApiCollection(context.getBean(WebSocketUserEndpoints.class));
        webSocketEndpointHandler.addApiCollection(context.getBean(WebSocketInfoEndpoints.class));
        webSocketEndpointHandler.postInit();

        httpEndpointHandler.addApiCollection(context.getBean(HttpInfoEndpoint.class));
        httpEndpointHandler.addApiCollection(context.getBean(HttpDaemonEndpoints.class, webSocketEndpointHandler.getApiList().getClients(), baseConfig.getApiToken()));
        httpEndpointHandler.postInit();

        try {
            daemonHandler.setSend(new WebSocketDaemonEndpoints(baseConfig.getApiToken()),
                    WebSocketDaemonEndpoints.class.getDeclaredMethod("send", ApiClient.class, String.class, ApiEndpointData.class, JsonObject.class));
        } catch (NoSuchMethodException e) {
            log.error("Error while setting daemon endpoint handling method.", e);
        }

        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("api", new RestApiLocationProvider(httpEndpointHandler.getApiList().getEndpoints(), null));
        httpServerCodec.addLocationProvider("ws", new WebSocketLocationProvider(webSocketEndpointHandler.getApiList().getEndpoints(),
                webSocketEndpointHandler.getApiList().getClients()::add));
        if (bootstrap.isDebug())
            httpServerCodec.addLocationProvider("playground", new PlaygroundLocationProvider(config.getWebsocketAddress(),
                    webSocketEndpointHandler.getApiList().getCollections().values()));

        serverService.addServer(new NettyServer("http",
                new InetSocketAddress(config.getHttpHost(), config.getHttpPort()),
                null, new NettyCodecHandler(httpServerCodec), eventLoopGroupService));

        System.getenv().forEach((name, value) -> {
            if (name.startsWith("DAEMON_")) daemonHandler.registerDaemon(name.substring(7).replace('_', '-').toLowerCase(), value);
        });
    }
}
