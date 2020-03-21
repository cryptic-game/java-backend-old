package net.cryptic_game.backend.server;

import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.server.NettyServerHandler;
import net.cryptic_game.backend.server.config.ServerConfig;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpointHandler;
import net.cryptic_game.backend.server.server.daemon.DaemonServerCodec;
import net.cryptic_game.backend.server.server.daemon.endpoints.DaemonInfoEndpoints;
import net.cryptic_game.backend.server.server.daemon.endpoints.DaemonUserEndpoints;
import net.cryptic_game.backend.server.server.http.HttpEndpointHandler;
import net.cryptic_game.backend.server.server.http.HttpServerCodec;
import net.cryptic_game.backend.server.server.websocket.WebSocketEndpointHandler;
import net.cryptic_game.backend.server.server.websocket.WebSocketServerCodec;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketInfoEndpoints;
import net.cryptic_game.backend.server.server.websocket.endpoints.WebSocketUserEndpoints;

import java.net.InetSocketAddress;

public class App extends AppBootstrap {

    private DaemonHandler daemonHandler;
    private NettyServerHandler serverHandler;

    private DaemonEndpointHandler daemonEndpointHandler;
    private WebSocketEndpointHandler webSocketEndpointHandler;
    private HttpEndpointHandler httpEndpointHandler;

    public App() {
        super(ServerConfig.CONFIG, "Java-Server");
    }

    public static void main(String[] args) {
        new App();
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

        this.webSocketEndpointHandler.addApiCollection(new WebSocketUserEndpoints());
        this.webSocketEndpointHandler.addApiCollection(new WebSocketInfoEndpoints());
        this.webSocketEndpointHandler.postInit();

        this.httpEndpointHandler.addApiCollection(new WebSocketInfoEndpoints());
    }

    @Override
    protected void init() {
        final boolean useUnixSocket = this.config.getAsBoolean(BaseConfig.USE_UNIX_SOCKET);
        this.serverHandler.addServer("daemon",
                useUnixSocket ? new DomainSocketAddress(this.config.getAsString(BaseConfig.UNIX_SOCKET_PATH)) : new InetSocketAddress("localhost", 4012),
                useUnixSocket, new DaemonServerCodec(this.daemonEndpointHandler));

        this.serverHandler.addServer("websocket",
                new InetSocketAddress(this.config.getAsString(ServerConfig.WEBSOCKET_HOST), this.config.getAsInt(ServerConfig.WEBSOCKET_PORT)),
                false, new WebSocketServerCodec(this.webSocketEndpointHandler));

        this.serverHandler.addServer("http",
                new InetSocketAddress(this.config.getAsString(ServerConfig.HTTP_HOST), this.config.getAsInt(ServerConfig.HTTP_PORT)),
                false, new HttpServerCodec(this.httpEndpointHandler));
    }

    @Override
    protected void start() {
        this.serverHandler.start();
    }
}
