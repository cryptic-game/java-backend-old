package net.cryptic_game.backend.server;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.api.netty.websocket.WebSocketLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyInetServer;
import net.cryptic_game.backend.base.netty.server.NettyServerHandler;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.http.HttpEndpointHandler;
import net.cryptic_game.backend.server.server.http.endpoints.HttpInfoEndpoint;
import net.cryptic_game.backend.server.server.playground.PlaygroundLocationProvider;
import net.cryptic_game.backend.server.server.websocket.WebSocketEndpointHandler;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketDaemonEndpoints;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketInfoEndpoints;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketUserEndpoints;

import java.net.InetSocketAddress;

@Slf4j
public final class App extends AppBootstrap {

    private static final ServerConfig SERVER_CONFIG = new ServerConfig();

    private WebSocketEndpointHandler webSocketEndpointHandler;
    private HttpEndpointHandler httpEndpointHandler;

    private EventLoopGroupHandler eventLoopGroupHandler;
    private NettyServerHandler serverHandler;

    private DaemonHandler daemonHandler;

    public App(final String[] args) {
        super(args, SERVER_CONFIG, "Java-Server");
    }

    public static void main(final String[] args) {
        log.info("Bootstrapping Java Server...");
        new App(args);
    }

    @Override
    protected void preInit() {
        this.webSocketEndpointHandler = new WebSocketEndpointHandler();
        this.httpEndpointHandler = new HttpEndpointHandler();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
        this.serverHandler = new NettyServerHandler();
        this.daemonHandler = new DaemonHandler(this.webSocketEndpointHandler.getApiList());
        try {
            this.daemonHandler.setSend(new WebSocketDaemonEndpoints(),
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
        this.httpEndpointHandler.postInit();
    }

    @Override
    protected void init() {
        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("api", new RestApiLocationProvider(this.httpEndpointHandler.getApiList().getEndpoints()));
        httpServerCodec.addLocationProvider("ws", new WebSocketLocationProvider(this.webSocketEndpointHandler.getApiList().getEndpoints()));
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
