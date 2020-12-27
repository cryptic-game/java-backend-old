package net.cryptic_game.backend.base.api.executor;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public final class ApiExecutor {

    private ApiExecutor() {
        throw new UnsupportedOperationException();
    }

    public static Mono<ApiResponse> execute(final Map<String, ApiEndpointData> endpoints, final ApiRequest request) {
        return Optional.ofNullable(endpoints.get(request.getEndpoint()))
                .map(endpoint -> ApiEndpointExecutor.execute(request, endpoint))
                .orElse(Mono.just(new ApiResponse(HttpResponseStatus.NOT_FOUND, "ENDPOINT")));
    }
}
