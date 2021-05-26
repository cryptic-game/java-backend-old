package net.cryptic_game.backend.admin.service.server_management;

import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;

import java.util.Optional;
import java.util.Set;

public interface EndpointService {

    Set<DisabledEndpoint> findDisabledEndpoints();

    Optional<DisabledEndpoint> findDisabledEndpoint(String path);

    Set<DisabledEndpoint> findDisabledEndpointsByReason(String reason);

    DisabledEndpoint disableEndpoint(String path, String reason);

    void enableEndpoint(String path);
}
