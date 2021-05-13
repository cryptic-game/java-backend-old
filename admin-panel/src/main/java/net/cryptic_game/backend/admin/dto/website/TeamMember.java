package net.cryptic_game.backend.admin.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamMember {

    private final UUID id;
    private final String name;
    private final Long githubId;
    private final UUID departmentId;
    private final OffsetDateTime joined;

    public TeamMember(
            @JsonProperty("name") final String name,
            @JsonProperty("githubId") final Long githubId,
            @JsonProperty("departmentId") final UUID departmentId,
            @JsonProperty("joined") final OffsetDateTime joined
    ) {
        this.id = null;
        this.name = name;
        this.githubId = githubId;
        this.departmentId = departmentId;
        this.joined = joined;
    }
}
