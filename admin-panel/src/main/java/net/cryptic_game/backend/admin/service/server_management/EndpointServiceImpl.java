package net.cryptic_game.backend.admin.service.server_management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.converter.server_management.DisabledEndpointConverter;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.dto.server_management.Endpoint;
import net.cryptic_game.backend.admin.exception.InternalServerErrorException;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public Mono<Endpoint> disableEndpoint(final String path, final DisabledEndpoint disabledEndpoint) {
        if (this.disabledEndpointRepository.findById(path).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "already disabled");
        }
        return this.serverCommunication.responseFromServer("/admin_panel/disable",
                new JSONObject(Map.of("id", path)))
                .map(response -> {
                    try {
                        final DisabledEndpointModel model = this.endpointConverter.toModel(disabledEndpoint);
                        model.setEndpointPath(path);
                        this.disabledEndpointRepository.save(model);
                        final Endpoint endpoint = this.objectMapper.readValue(response, Endpoint.class);
                        endpoint.setReason(disabledEndpoint.getReason());
                        return endpoint;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new InternalServerErrorException("");
                    }
                });
    }

    @Override
    public Mono<Endpoint> enableEndpoint(final String path) {
        if (this.disabledEndpointRepository.findById(path).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not disabled");
        }
        return this.serverCommunication.responseFromServer("/admin_panel/enable",
                new JSONObject(Map.of("id", path)))
                .map(response -> {
                    try {
                        this.disabledEndpointRepository.deleteById(path);
                        return this.objectMapper.readValue(response, Endpoint.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new InternalServerErrorException("");
                    }
                });
    }

    @Override
    public Mono<Endpoint> endpointInfo(final String path) {
        return this.serverCommunication.responseFromServer("/admin_panel/info",
                new JSONObject(Map.of("id", path)))
                .map(response -> {
                    try {
                        final Endpoint endpoint = this.objectMapper.readValue(response, Endpoint.class);
                        if (endpoint.isDisabled()) {
                            endpoint.setReason(
                                    this.disabledEndpointRepository
                                            .findById(path)
                                            .map(DisabledEndpointModel::getReason)
                                            .orElse("No reason available"));
                        }
                        return endpoint;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new InternalServerErrorException("");
                    }
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
