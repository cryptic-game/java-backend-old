package net.cryptic_game.backend.server;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.api.netty.websocket.WebSocketLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyInetServer;
import net.cryptic_game.backend.base.netty.server.NettyServerHandler;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.http.HttpDaemonEndpoints;
import net.cryptic_game.backend.server.server.http.HttpInfoEndpoint;
import net.cryptic_game.backend.server.server.playground.PlaygroundLocationProvider;
import net.cryptic_game.backend.server.server.websocket.WebSocketDaemonEndpoints;
import net.cryptic_game.backend.server.server.websocket.WebSocketInfoEndpoints;
import net.cryptic_game.backend.server.server.websocket.WebSocketUserEndpoints;

import java.net.InetSocketAddress;

@Slf4j
public final class App extends AppBootstrap {

    private static final ServerConfig SERVER_CONFIG = new ServerConfig();

    private ApiEndpointHandler webSocketEndpointHandler;
    private ApiEndpointHandler httpEndpointHandler;

    private EventLoopGroupHandler eventLoopGroupHandler;
    private NettyServerHandler serverHandler;

    private DaemonHandler daemonHandler;

    public App(final String[] args) {
        super(args, SERVER_CONFIG, "Java-Server");
    }

    public static void main(final String[] args) {
        new App(args);
    }

    @Override
    protected void preInit() {
        this.webSocketEndpointHandler = new ApiEndpointHandler();
        this.httpEndpointHandler = new ApiEndpointHandler();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
        this.serverHandler = new NettyServerHandler();
        this.daemonHandler = new DaemonHandler(this.webSocketEndpointHandler.getApiList(), this.getConfig().getApiToken());
        try {
            this.daemonHandler.setSend(new WebSocketDaemonEndpoints(this.getConfig().getApiToken()),
                    WebSocketDaemonEndpoints.class.getDeclaredMethod("send", ApiClient.class, String.class, ApiEndpointData.class, JsonObject.class));
        } catch (NoSuchMethodException e) {
            log.error("Error while setting daemon endpoint handling method.", e);
        }
    }

    @Override
    protected void initApi() {
        this.webSocketEndpointHandler.addApiCollection(new WebSocketUserEndpoints());
        this.webSocketEndpointHandler.addApiCollection(new WebSocketInfoEndpoints());
        this.webSocketEndpointHandler.postInit();

        this.httpEndpointHandler.addApiCollection(new HttpInfoEndpoint());
        this.httpEndpointHandler.addApiCollection(new HttpDaemonEndpoints(this.webSocketEndpointHandler.getApiList().getClients()));
        this.httpEndpointHandler.postInit();
    }

    @Override
    protected void init() {
        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("api", new RestApiLocationProvider(this.httpEndpointHandler.getApiList().getEndpoints(), null));
        httpServerCodec.addLocationProvider("ws", new WebSocketLocationProvider(this.webSocketEndpointHandler.getApiList().getEndpoints(),
                this.webSocketEndpointHandler.getApiList().getClients()::add));
        if (!this.getConfig().isProductive())
            httpServerCodec.addLocationProvider("playground", new PlaygroundLocationProvider(SERVER_CONFIG.getWebsocketAddress(),
                    this.webSocketEndpointHandler.getApiList().getCollections().values()));
        this.serverHandler.addServer(new NettyInetServer("http",
                new InetSocketAddress(SERVER_CONFIG.getHttpHost(), SERVER_CONFIG.getHttpPort()),
                null, new NettyCodecHandler(httpServerCodec), this.eventLoopGroupHandler));
    }

    @Override
    protected void start() {
        this.serverHandler.start();
        this.daemonHandler.registerDaemon("java-daemon", SERVER_CONFIG.getJavaDaemonAddress());
    }


}
