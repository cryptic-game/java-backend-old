package net.cryptic_game.backend.daemon;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.daemon.client.NettyClientHandler;
import net.cryptic_game.backend.daemon.config.DaemonConfig;

public class App extends AppBootstrap {

    private NettyClientHandler clientHandler;

    public App() {
        super(DaemonConfig.CONFIG, "Java-Daemon");
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void init() {
        this.clientHandler = new NettyClientHandler();
    }

    @Override
    protected void start() {
        this.clientHandler.start();
    }

    @Override
    protected void initApi() {

    }
}
