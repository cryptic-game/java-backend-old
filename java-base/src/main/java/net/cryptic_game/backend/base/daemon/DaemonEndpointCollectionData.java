package net.cryptic_game.backend.base.daemon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class DaemonEndpointCollectionData extends ApiEndpointCollectionData {

    private Daemon daemon;

    public DaemonEndpointCollectionData(@NotNull final String id,
                                        @NotNull final String description,
                                        final boolean internal,
                                        final boolean disabled,
                                        @NotNull final ApiType apiType,
                                        @NotNull final Map<String, ApiEndpointData> endpoints) {
        super(id, description, apiType, internal, disabled, endpoints);
    }
}
