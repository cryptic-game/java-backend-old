package net.cryptic_game.backend.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamDepartment {

    private final UUID id;
    private final String name;
    private final String description;

    public TeamDepartment(
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description
    ) {
        this.id = null;
        this.name = name;
        this.description = description;
    }
}
