package net.cryptic_game.backend.admin.controller.server_management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;

import net.cryptic_game.backend.admin.service.server_management.EndpointService;
import net.cryptic_game.backend.admin.service.server_management.ServerCommunication;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;


@RestController
@RequiredArgsConstructor
public class EndpointControllerImpl implements EndpointController {

    private final EndpointService endpointService;
    private final ServerCommunication serverCommunication;

    @Override
    public Mono<ObjectNode> findAll() {
        return this.serverCommunication.responseFromServer("/admin_panel/endpoints",
                new JSONObject());
    }

    @Override
    public Mono<ObjectNode> getInfo(final String path) {
        return this.endpointService.endpointInfo(path);
    }

    @Override
    public Mono<ObjectNode> enableEndpoint(final String path) {
        return this.endpointService.enableEndpoint(path);
    }

    @Override
    public Mono<ObjectNode> disableEndpoint(final String path, final DisabledEndpoint disabledEndpoint) {
        return this.endpointService.disableEndpoint(path, disabledEndpoint);
    }

    @Override
    public Set<DisabledEndpoint> findAllDisabledEndpoints() {
        return this.endpointService.findDisabledEndpoints();
    }

    @Override
    public Mono<ObjectNode> edit(final String path, final DisabledEndpoint disabledEndpoint) {
        this.endpointService.edit(path, disabledEndpoint);
        return this.endpointService.endpointInfo(path);
    }
}
