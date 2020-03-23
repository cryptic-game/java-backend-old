package net.cryptic_game.backend.daemon;

import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.client.NettyClient;
import net.cryptic_game.backend.base.netty.client.NettyClientHandler;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;
import net.cryptic_game.backend.daemon.client.daemon.DaemonClientCodec;
import net.cryptic_game.backend.daemon.config.DaemonConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

public class App extends AppBootstrap {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private NettyClientHandler clientHandler;
    private NettyClient client;

    private DaemonEndpointHandler daemonEndpointHandler;
    private DaemonBootstrapper daemonBootstrapper;

    public App() {
        super(DaemonConfig.CONFIG, "Java-Daemon");
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void preInit() {
        this.daemonEndpointHandler = new DaemonEndpointHandler();
        this.daemonBootstrapper = new DaemonBootstrapper(this.getName(), this.daemonEndpointHandler);
    }

    @Override
    protected void initApi() {
        for (Class<? extends ApiEndpointCollection> modelClass : new Reflections("net.cryptic_game.backend.endpoints").getSubTypesOf(ApiEndpointCollection.class)) {
            try {
                this.daemonEndpointHandler.addApiCollection(modelClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void init() {
        this.clientHandler = new NettyClientHandler();

        final boolean useUnixSocket = this.config.getAsBoolean(BaseConfig.USE_UNIX_SOCKET);
        this.client = this.clientHandler.addClient("daemon",
                useUnixSocket ? new DomainSocketAddress(this.config.getAsString(BaseConfig.UNIX_SOCKET_PATH)) : new InetSocketAddress("localhost", 4012),
                useUnixSocket, new DaemonClientCodec(this.daemonEndpointHandler));
    }

    @Override
    protected void start() {
        this.clientHandler.start();

        new Thread(() -> {
            log.info("Waiting ten seconds to connect to the server.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("Sending request for registering on the server.");
            this.daemonBootstrapper.sendRegisterPackage(this.client.getChannel());
        }).start();
    }
}
