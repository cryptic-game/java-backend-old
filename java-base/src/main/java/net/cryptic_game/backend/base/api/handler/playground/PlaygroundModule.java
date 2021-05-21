package net.cryptic_game.backend.base.api.handler.playground;

import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.network.server.http.HttpServerService;
import net.cryptic_game.backend.base.network.server.http.route.FileHttpRoute;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

@Configuration
public class PlaygroundModule {

    private final TreeSet<ApiEndpointCollectionData> collections;

    public PlaygroundModule(final PlaygroundConfig config, final HttpServerService httpServerService) {
        this.collections = new TreeSet<>();

        httpServerService.getRoutes().addRoute("playground", new FileHttpRoute(new File("www")));

//    httpServerModule.getServer().getRoutes()
//      .get(config.getPath(), (request, response) -> response.sendFile(Path.of("www/playground/index.html")))
//      .directory( config.getPath(), Path.of("www/playground"));
    }

    public final void addCollections(final Collection<ApiEndpointCollectionData> collections) {
        this.collections.addAll(collections);
    }
}
