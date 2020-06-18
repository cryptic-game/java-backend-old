package net.cryptic_game.backend.daemon;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.netty.client.NettyClient;
import net.cryptic_game.backend.base.netty.client.NettyClientHandler;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;
import net.cryptic_game.backend.daemon.client.daemon.DaemonClientCodec;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

public final class App extends AppBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final DaemonConfig DAEMON_CONFIG = new DaemonConfig();

    private NettyClientHandler clientHandler;
    private NettyClient client;

    private DaemonEndpointHandler daemonEndpointHandler;
    private DaemonBootstrapper daemonBootstrapper;

    public App(final String[] args) {
        super(args, DAEMON_CONFIG, "Java-Daemon");
    }

    public static void main(final String[] args) {
        LOG.info("Bootstrapping Java Daemon...");
        new App(args);
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
        this.daemonEndpointHandler.postInit();
    }

    @Override
    protected void init() {
        this.clientHandler = new NettyClientHandler();

        final boolean useUnixSocket = this.getConfig().isUseUnixSocket();
        this.client = this.clientHandler.addClient("daemon",
                useUnixSocket ? new DomainSocketAddress(this.getConfig().getUnixSocketPath()) : new InetSocketAddress("localhost", 4012),
                useUnixSocket, new DaemonClientCodec(this.daemonEndpointHandler), this::onConnect);
    }

    @Override
    protected void start() {
        this.clientHandler.start();
    }

    public void onConnect(final Channel channel) {
        LOG.info("Sending request for registering on the server.");
        this.daemonBootstrapper.sendRegisterPackage(channel);
    }
}
