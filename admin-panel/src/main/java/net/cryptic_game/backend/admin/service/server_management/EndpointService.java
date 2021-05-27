package net.cryptic_game.backend.admin.service.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

public interface EndpointService {

    Set<DisabledEndpoint> findDisabledEndpoints();

    Optional<DisabledEndpoint> findDisabledEndpoint(String path);

    Mono<JSONObject> disableEndpoint(String path, DisabledEndpoint disabledEndpoint);

    Mono<JSONObject> enableEndpoint(String path);

    Mono<JSONObject> endpointInfo(String path);

    void edit(String path, DisabledEndpoint template);
}
