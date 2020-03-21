package net.cryptic_game.backend.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.client.NettyClient;
import net.cryptic_game.backend.base.netty.client.NettyClientHandler;
import net.cryptic_game.backend.daemon.client.daemon.DaemonClientCodec;
import net.cryptic_game.backend.daemon.config.DaemonConfig;

import java.net.InetSocketAddress;

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
    protected void preInit() {
    }

    @Override
    protected void initApi() {
    }

    @Override
    protected void init() {
        this.clientHandler = new NettyClientHandler();

        final boolean useUnixSocket = this.config.getAsBoolean(BaseConfig.USE_UNIX_SOCKET);
        this.client = this.clientHandler.addClient("daemon",
                useUnixSocket ? new DomainSocketAddress(this.config.getAsString(BaseConfig.UNIX_SOCKET_PATH)) : new InetSocketAddress("localhost", 4012),
                useUnixSocket, new DaemonClientCodec());
    }

    @Override
    protected void start() {
        this.clientHandler.start();
        final JsonObject json = new JsonObject();

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            json.addProperty("test", 1);
            this.client.getChannel().writeAndFlush(json);
        }).start();
    }
}
