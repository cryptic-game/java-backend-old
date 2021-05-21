package net.cryptic_game.backend.daemon.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.rest.RestApiInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ApiEndpointCollection(id = "daemon", description = "Informational endpoints about the daemon.", type = ApiType.REST)
public final class DaemonInfoEndpoints {

    private final ApplicationContext context;
    private ApiResponse endpointsResponse;

    @ApiEndpoint(id = "endpoints", description = "All available endpoints of the daemon")
    public ApiResponse endpoints() {
        System.out.println(this.context.getBean(RestApiInitializer.class).getCollections());

        if (this.endpointsResponse == null)
            this.endpointsResponse = new ApiResponse(HttpResponseStatus.OK, this.context.getBean(RestApiInitializer.class).getCollections()
                    .stream()
                    .filter(apiEndpointCollectionData -> !apiEndpointCollectionData.getId().equals("daemon"))
                    .collect(Collectors.toSet()));
        return this.endpointsResponse;
    }
}
