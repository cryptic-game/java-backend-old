package net.cryptic_game.backend.admin.service.server_management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

public interface EndpointService {

    Set<DisabledEndpoint> findDisabledEndpoints();

    Optional<DisabledEndpoint> findDisabledEndpoint(String path);

    Mono<ObjectNode> disableEndpoint(String path, DisabledEndpoint disabledEndpoint);

    Mono<ObjectNode> enableEndpoint(String path);

    Mono<ObjectNode> endpointInfo(String path);

    void edit(String path, DisabledEndpoint template);
}
