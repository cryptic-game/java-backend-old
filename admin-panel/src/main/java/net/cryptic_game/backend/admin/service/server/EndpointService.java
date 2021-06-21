package net.cryptic_game.backend.admin.service.server;

import net.cryptic_game.backend.admin.dto.server.ServerEndpoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EndpointService {

    Flux<ServerEndpoint> findEndpoints();

    Mono<ServerEndpoint> disableEndpoint(String id);

    Mono<ServerEndpoint> enableEndpoint(String id);
}
