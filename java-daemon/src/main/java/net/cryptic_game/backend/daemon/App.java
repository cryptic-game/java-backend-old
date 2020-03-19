package net.cryptic_game.backend.daemon;

import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.daemon.client.NettyClient;
import net.cryptic_game.backend.daemon.client.NettyClientHandler;
import net.cryptic_game.backend.daemon.client.daemon.DaemonClientCodec;
import net.cryptic_game.backend.daemon.config.DaemonConfig;

public class App extends AppBootstrap {

    private NettyClientHandler clientHandler;

    private NettyClient client;

    public App() {
        super(DaemonConfig.CONFIG, "Java-Daemon");
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void init() {
        this.clientHandler = new NettyClientHandler();

        this.client = this.clientHandler.addClient("daemon",
                new DomainSocketAddress(this.config.getAsString(BaseConfig.UNIX_SOCKET)),
                new DaemonClientCodec());
    }

    @Override
    protected void start() {
        this.clientHandler.start();
    }

    @Override
    protected void initApi() {
    }
}
