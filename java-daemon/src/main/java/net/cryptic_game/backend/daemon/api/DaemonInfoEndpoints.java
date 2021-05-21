package net.cryptic_game.backend.daemon.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.DaemonAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.rest.RestApiService;
import org.springframework.stereotype.Component;

import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ApiEndpointCollection(id = "daemon", description = "Informational endpoints about the daemon.", type = ApiType.REST, authenticator = DaemonAuthenticator.class)
public final class DaemonInfoEndpoints {

    private final Bootstrap bootstrap;
    private ApiResponse endpointsResponse;

    @ApiEndpoint(id = "endpoints", description = "All available endpoints of the daemon")
    public ApiResponse endpoints() {
        if (this.endpointsResponse == null)
            this.endpointsResponse = new ApiResponse(HttpResponseStatus.OK, this.bootstrap.getContextHandler().getBean(RestApiService.class).getCollections()
                    .stream()
                    .filter(apiEndpointCollectionData -> !apiEndpointCollectionData.getId().equals("daemon"))
                    .collect(Collectors.toCollection((Supplier<TreeSet<ApiEndpointCollectionData>>) TreeSet::new)));
        return this.endpointsResponse;
    }
}
