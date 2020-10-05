package net.cryptic_game.backend.daemon.api;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.handler.rest.RestApiModule;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ApiEndpointCollection(id = "daemon", description = "Informational endpoints about the daemon.")
public final class DaemonInfoEndpoints {

    private final Bootstrap bootstrap;
    private ApiResponse endpointsResponse;

    @ApiEndpoint(id = "endpoints", description = "All available endpoints of the daemon")
    public ApiResponse endpoints() {
        if (this.endpointsResponse == null)
            this.endpointsResponse = new ApiResponse(ApiResponseStatus.OK, this.bootstrap.getContextHandler().getBean(RestApiModule.class).getCollections()
                    .stream().filter(apiEndpointCollectionData -> !apiEndpointCollectionData.getId().equals("daemon")).collect(Collectors.toUnmodifiableSet()));
        return this.endpointsResponse;
    }
}
