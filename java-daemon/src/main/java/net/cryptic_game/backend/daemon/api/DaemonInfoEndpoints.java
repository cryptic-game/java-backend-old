package net.cryptic_game.backend.daemon.api;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointHandler;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public final class DaemonInfoEndpoints extends ApiEndpointCollection {

    private final ApiEndpointHandler daemonEndpointHandler;
    private ApiResponse endpointsResponse;

    public DaemonInfoEndpoints(final ApiEndpointHandler daemonEndpointHandler) {
        super("daemon", "Informational endpoints about the daemon.");
        this.daemonEndpointHandler = daemonEndpointHandler;
    }

    @ApiEndpoint(value = "endpoints", description = "All available endpoints of the daemon")
    public ApiResponse endpoints() {
        if (this.endpointsResponse == null)
            this.endpointsResponse = new ApiResponse(ApiResponseType.OK, this.daemonEndpointHandler.getApiList().getCollections().values()
                    .stream().filter(apiEndpointCollectionData -> !apiEndpointCollectionData.getName().equals("daemon")).collect(Collectors.toUnmodifiableSet()));
        return this.endpointsResponse;
    }
}
