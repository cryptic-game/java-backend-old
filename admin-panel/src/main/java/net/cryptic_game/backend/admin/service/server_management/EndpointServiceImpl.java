package net.cryptic_game.backend.admin.service.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.converter.server_management.DisabledEndpointConverter;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.model.server_management.DisabledEndpointModel;
import net.cryptic_game.backend.admin.repository.server_management.DisabledEndpointRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {

    private final DisabledEndpointRepository disabledEndpointRepository;
    private final DisabledEndpointConverter endpointConverter;
    private final ServerCommunicationImpl serverCommunication;

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
    public Set<DisabledEndpoint> findDisabledEndpointsByReason(final String reason) {
        return this.disabledEndpointRepository.findAllByReason(reason)
                .stream().map(this.endpointConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public DisabledEndpoint disableEndpoint(final String path, final String reason) {
        this.serverCommunication.responseFromServer("/admin_panel/disable", new JSONObject(Map.of("id", path)));

        return this.endpointConverter.toDto(
                this.disabledEndpointRepository.save(
                        new DisabledEndpointModel(path, reason)
                ));
    }

    @Override
    public void enableEndpoint(final String path) {
        this.serverCommunication.responseFromServer("/admin_panel/enable", new JSONObject(Map.of("id", path)));

        this.disabledEndpointRepository.deleteById(path);
    }
}
