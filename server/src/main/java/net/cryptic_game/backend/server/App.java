package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.server.config.ServerConfig;
import net.cryptic_game.backend.server.server.NettyServerHandler;
import net.cryptic_game.backend.server.server.http.HttpCodec;
import net.cryptic_game.backend.server.server.websocket.WebSocketCodec;

public class App extends AppBootstrap {

    private NettyServerHandler serverHandler;

    public App() {
        super(ServerConfig.CONFIG);
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void init() {
        this.serverHandler = new NettyServerHandler();
        this.serverHandler.addServer("websocket",
                this.config.getAsString(ServerConfig.WEBSOCKET_HOST),
                this.config.getAsInt(ServerConfig.WEBSOCKET_PORT),
                new WebSocketCodec());
        this.serverHandler.addServer("http",
                this.config.getAsString(ServerConfig.HTTP_HOST),
                this.config.getAsInt(ServerConfig.HTTP_PORT),
                new HttpCodec());
    }

    @Override
    protected void start() {
        this.serverHandler.start();
    }
}
