package net.cryptic_game.backend.server;

import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.netty.server.NettyServerHandler;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpointHandler;
import net.cryptic_game.backend.server.server.daemon.DaemonServerCodec;
import net.cryptic_game.backend.server.server.daemon.endpoints.DaemonInfoEndpoints;
import net.cryptic_game.backend.server.server.daemon.endpoints.DaemonUserEndpoints;
import net.cryptic_game.backend.server.server.http.HttpEndpointHandler;
import net.cryptic_game.backend.server.server.http.HttpServerCodec;
import net.cryptic_game.backend.server.server.websocket.WebSocketEndpointHandler;
import net.cryptic_game.backend.server.server.websocket.WebSocketServerCodec;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketDaemonEndpoints;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketInfoEndpoints;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketUserEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class App extends AppBootstrap {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final ServerConfig serverConfig = new ServerConfig();

    private DaemonHandler daemonHandler;
    private NettyServerHandler serverHandler;

    private DaemonEndpointHandler daemonEndpointHandler;
    private WebSocketEndpointHandler webSocketEndpointHandler;
    private HttpEndpointHandler httpEndpointHandler;

    public App(final String[] args) {
        super(args, serverConfig, "Java-Server");
    }

    public static void main(String[] args) {
        log.info("Bootstrapping Java Server...");
        new App(args);
    }

    @Override
    protected void preInit() {
        this.daemonHandler = new DaemonHandler();
        this.serverHandler = new NettyServerHandler();

        this.daemonEndpointHandler = new DaemonEndpointHandler();
        this.webSocketEndpointHandler = new WebSocketEndpointHandler();
        this.httpEndpointHandler = new HttpEndpointHandler();
    }

    @Override
    protected void initApi() {
        this.daemonEndpointHandler.addApiCollection(new DaemonInfoEndpoints(this.daemonHandler));
        this.daemonEndpointHandler.addApiCollection(new DaemonUserEndpoints());
        this.daemonEndpointHandler.postInit();

        this.webSocketEndpointHandler.addApiCollection(new WebSocketUserEndpoints());
        this.webSocketEndpointHandler.addApiCollection(new WebSocketInfoEndpoints());
        this.webSocketEndpointHandler.addApiCollection(new WebSocketDaemonEndpoints(this.daemonHandler));
        this.webSocketEndpointHandler.postInit();

        this.httpEndpointHandler.addApiCollection(new WebSocketInfoEndpoints());
        this.httpEndpointHandler.postInit();
    }

    @Override
    protected void init() {
        final boolean useUnixSocket = this.getConfig().isUseUnixSocket();
        this.serverHandler.addServer("daemon",
                useUnixSocket ? new DomainSocketAddress(this.getConfig().getUnixSocketPath()) : new InetSocketAddress("localhost", 4012),
                useUnixSocket, new DaemonServerCodec(this.daemonHandler, this.daemonEndpointHandler));

        this.serverHandler.addServer("websocket",
                new InetSocketAddress(serverConfig.getWebsocketHost(), serverConfig.getWebsocketPort()),
                false, new WebSocketServerCodec(this.webSocketEndpointHandler));

        this.serverHandler.addServer("http",
                new InetSocketAddress(serverConfig.getHttpHost(), serverConfig.getHttpPort()),
                false, new HttpServerCodec(this.httpEndpointHandler));
    }

    @Override
    protected void start() {
        this.serverHandler.start();
    }

    public WebSocketEndpointHandler getWebSocketEndpointHandler() {
        return webSocketEndpointHandler;
    }
}
