package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupService;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyServerService;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.daemon.api.DaemonInfoEndpoints;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

@Slf4j
public final class App extends AppBootstrap {

    private static final DaemonConfig DAEMON_CONFIG = new DaemonConfig();

    private NettyServerService serverHandler;
    private EventLoopGroupService eventLoopGroupService;
    private ApiEndpointHandler daemonEndpointHandler;

    public App(final String[] args) {
        super(args, DAEMON_CONFIG, "Java-Daemon");
    }

    public static void main(final String[] args) {
        new App(args);
    }

    @Override
    protected void preInit() {
        DaemonUtils.setServerUrl(DAEMON_CONFIG.getServerUrl());
        this.daemonEndpointHandler = new ApiEndpointHandler();
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
        this.daemonEndpointHandler.addApiCollection(new DaemonInfoEndpoints(this.daemonEndpointHandler));
        this.daemonEndpointHandler.postInit();
    }

    @Override
    protected void init() {
        this.serverHandler = new NettyServerService();
        this.eventLoopGroupService = new EventLoopGroupService();

        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("/", new RestApiLocationProvider(
                this.daemonEndpointHandler.getApiList().getEndpoints(),
                this.getConfig().getApiToken()));

        this.serverHandler.addServer(new NettyInetServer("daemon",
                new InetSocketAddress(DAEMON_CONFIG.getHttpHost(), DAEMON_CONFIG.getHttpPort()),
                null, new NettyCodecHandler(httpServerCodec), this.eventLoopGroupService));
    }

    @Override
    protected void start() {
        this.serverHandler.start();
    }
}
