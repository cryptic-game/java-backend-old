package net.cryptic_game.backend.admin;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.netty.rest.RestApiLocationProvider;
import net.cryptic_game.backend.base.netty.EventLoopGroupService;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;
import net.cryptic_game.backend.base.netty.codec.http.HttpServerCodec;
import net.cryptic_game.backend.base.netty.server.NettyServer;
import net.cryptic_game.backend.base.netty.server.NettyServerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.InetSocketAddress;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "net.cryptic_game.backend.admin.data.sql")
public class AdminPanelBootstrap {

    public AdminPanelBootstrap(final AdminPanelConfig adminPanelConfig,
                               final ApplicationContext context,
                               final NettyServerService serverService,
                               final EventLoopGroupService eventLoopGroupService) {

        final ApiEndpointHandler adminPanelEndpointHandler = new ApiEndpointHandler();
        context.getBeansOfType(ApiEndpointCollection.class).forEach((name, collection) -> adminPanelEndpointHandler.addApiCollection(collection));
        adminPanelEndpointHandler.postInit();

        final HttpServerCodec httpServerCodec = new HttpServerCodec();
        httpServerCodec.addLocationProvider("/", new RestApiLocationProvider(adminPanelEndpointHandler.getApiList().getEndpoints(), null));

        serverService.addServer(new NettyServer("admin-panel",
                new InetSocketAddress(adminPanelConfig.getHttpHost(), adminPanelConfig.getHttpPort()),
                null, new NettyCodecHandler(httpServerCodec), eventLoopGroupService));
    }
}
