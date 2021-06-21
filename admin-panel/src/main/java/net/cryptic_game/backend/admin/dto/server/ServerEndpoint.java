package net.cryptic_game.backend.admin.dto.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ServerEndpoint {

    private final String id;
    private final String description;
    private final boolean disabled;

    public ServerEndpoint(
            @JsonProperty("id") final String id,
            @JsonProperty("description") final String description,
            @JsonProperty("disabled") final boolean disabled
    ) {
        this.id = id;
        this.description = description;
        this.disabled = disabled;
    }
}
