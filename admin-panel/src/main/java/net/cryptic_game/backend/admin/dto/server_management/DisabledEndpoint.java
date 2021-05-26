package net.cryptic_game.backend.admin.dto.server_management;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DisabledEndpoint {

    private final String endpointPath;
    private final String reason;

    public DisabledEndpoint(@JsonProperty("endpointPath") final String endpointPath,
                            @JsonProperty("reason") final String reason) {
        this.endpointPath = endpointPath;
        this.reason = reason;
    }
}
