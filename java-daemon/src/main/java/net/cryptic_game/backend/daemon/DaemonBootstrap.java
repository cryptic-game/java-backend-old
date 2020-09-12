package net.cryptic_game.backend.daemon;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupService;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyServer;
import net.cryptic_game.backend.base.netty.server.NettyServerService;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Slf4j
@Configuration
public class DaemonBootstrap {

    public DaemonBootstrap(final BaseConfig baseConfig,
                           final DaemonConfig daemonConfig,
                           final ApplicationContext context,
                           final NettyServerService serverService,
                           final EventLoopGroupService eventLoopGroupService) {

        DaemonUtils.setServerAddress(daemonConfig.getServerAddress());

        final ApiEndpointHandler daemonEndpointHandler = new ApiEndpointHandler();
        context.getBeansOfType(ApiEndpointCollection.class).forEach((name, collection) -> daemonEndpointHandler.addApiCollection(collection));
        daemonEndpointHandler.postInit();

        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("/", new RestApiLocationProvider(
                daemonEndpointHandler.getApiList().getEndpoints(),
                baseConfig.getApiToken()));

        serverService.addServer(new NettyServer("daemon",
                new InetSocketAddress(daemonConfig.getHttpHost(), daemonConfig.getHttpPort()),
                null, new NettyCodecHandler(httpServerCodec), eventLoopGroupService));
    }
}
