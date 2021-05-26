package net.cryptic_game.backend.admin.converter.server_management;

import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.model.server_management.DisabledEndpointModel;
import net.getnova.framework.core.Converter;
import org.springframework.stereotype.Component;

@Component
public class DisabledEndpointConverter implements Converter<DisabledEndpointModel, DisabledEndpoint> {

    @Override
    public DisabledEndpointModel toModel(final DisabledEndpoint dto) {
        return new DisabledEndpointModel(
                dto.getEndpointPath(),
                dto.getReason()
        );
    }

    @Override
    public DisabledEndpoint toDto(final DisabledEndpointModel model) {
        return new DisabledEndpoint(
                model.getEndpointPath(),
                model.getReason()
        );
    }

    @Override
    public void override(final DisabledEndpointModel model, final DisabledEndpoint dto) {
        model.setReason(dto.getReason());
    }

    @Override
    public void merge(final DisabledEndpointModel model, final DisabledEndpoint dto) {
        if (dto.getReason() != null) {
            model.setReason(dto.getReason());
        }
    }
}
