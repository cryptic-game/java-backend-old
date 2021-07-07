package net.cryptic_game.backend.admin.service.server;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.dto.server.ServerEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {

    private final WebClient client;

    @Override
    public Flux<ServerEndpoint> findEndpoints() {
        return this.client.get()
                .uri("/admin_panel/endpoints")
                .exchangeToFlux((response) ->
                        response.statusCode().is2xxSuccessful()
                                ? response.bodyToFlux(ServerEndpoint.class)
                                : response.createException().flatMapMany(Flux::error)
                );
    }

    @Override
    public Mono<ServerEndpoint> disableEndpoint(final String id) {
        return this.client.post()
                .uri("/admin_panel/disable")
                .bodyValue(Map.of("id", id))
                .exchangeToMono(this::handleResponse);

    }

    @Override
    public Mono<ServerEndpoint> enableEndpoint(final String id) {
        return this.client.post()
                .uri("/admin_panel/enable")
                .bodyValue(Map.of("id", id))
                .exchangeToMono(this::handleResponse);
    }

    private Mono<ServerEndpoint> handleResponse(final ClientResponse response) {
        return response.statusCode().is2xxSuccessful()
                ? response.bodyToMono(ServerEndpoint.class)
                : response.createException().flatMap(Mono::error);
    }
}
