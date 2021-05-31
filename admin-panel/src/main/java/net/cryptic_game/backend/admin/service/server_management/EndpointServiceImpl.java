package net.cryptic_game.backend.admin.service.server_management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.converter.server_management.DisabledEndpointConverter;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.model.server_management.DisabledEndpointModel;
import net.cryptic_game.backend.admin.repository.server_management.DisabledEndpointRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {

    private final DisabledEndpointRepository disabledEndpointRepository;
    private final DisabledEndpointConverter endpointConverter;
    private final ServerCommunication serverCommunication;

    @Override
    public Set<DisabledEndpoint> findDisabledEndpoints() {
        return this.disabledEndpointRepository.findAll()
                .stream().map(this.endpointConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<DisabledEndpoint> findDisabledEndpoint(final String path) {
        return this.disabledEndpointRepository.findById(path)
                .map(this.endpointConverter::toDto);
    }

    @Override
    public Mono<ObjectNode> disableEndpoint(final String path, final DisabledEndpoint disabledEndpoint) {
        if (this.disabledEndpointRepository.findById(path).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "already disabled");
        }
        return this.serverCommunication.responseFromServer("/admin_panel/disable",
                new JSONObject(Map.of("id", path)))
                .doOnSuccess(endpoint -> {
                    final DisabledEndpointModel model = this.endpointConverter.toModel(disabledEndpoint);
                    model.setEndpointPath(path);
                    this.disabledEndpointRepository.save(model);
                    endpoint.put("reason", disabledEndpoint.getReason());
                });
    }

    @Override
    public Mono<ObjectNode> enableEndpoint(final String path) {
        if (this.disabledEndpointRepository.findById(path).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not disabled");
        }
        return this.serverCommunication.responseFromServer("/admin_panel/enable",
                new JSONObject(Map.of("id", path)))
                .doOnSuccess(i -> this.disabledEndpointRepository.deleteById(path));
    }

    @Override
    public Mono<ObjectNode> endpointInfo(final String path) {
        return this.serverCommunication.responseFromServer("/admin_panel/info",
                new JSONObject(Map.of("id", path)))
                .doOnSuccess(endpoint -> {
                    Optional<DisabledEndpointModel> disabledEndpointModel = this.disabledEndpointRepository.findById(path);
                    disabledEndpointModel.ifPresent(model -> endpoint.put("reason", model.getReason()));
                });
    }

    @Override
    public void edit(final String path, final DisabledEndpoint template) {
        this.disabledEndpointRepository.findById(path)
                .ifPresentOrElse(i -> {
                    this.endpointConverter.override(i, template);
                    this.disabledEndpointRepository.save(i);
                }, () -> {
                    throw new NotFoundException(path, "Endpoint not disabled or nonexistent");
                });
    }
}
