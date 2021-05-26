package net.cryptic_game.backend.admin.service.server_management;

import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.dto.server_management.Endpoint;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

public interface EndpointService {

    Set<DisabledEndpoint> findDisabledEndpoints();

    Optional<DisabledEndpoint> findDisabledEndpoint(String path);

    Mono<Endpoint> disableEndpoint(String path, DisabledEndpoint disabledEndpoint);

    Mono<Endpoint> enableEndpoint(String path);

    Mono<Endpoint> endpointInfo(String path);

    void edit(String path, DisabledEndpoint template);
}
