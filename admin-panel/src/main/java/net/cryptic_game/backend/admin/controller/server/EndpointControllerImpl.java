package net.cryptic_game.backend.admin.controller.server;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.service.server.EndpointService;
import net.cryptic_game.backend.dto.server.ServerEndpoint;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class EndpointControllerImpl implements EndpointController {

    private final EndpointService endpointService;

    @Override
    public Flux<ServerEndpoint> findAll() {
        return this.endpointService.findEndpoints();
    }

    @Override
    public Mono<ServerEndpoint> disableEndpoint(final String id) {
        return this.endpointService.disableEndpoint(id);
    }

    @Override
    public Mono<ServerEndpoint> enableEndpoint(final String id) {
        return this.endpointService.enableEndpoint(id);
    }
}
