package net.cryptic_game.backend.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.getnova.framework.core.Validatable;
import net.getnova.framework.core.exception.ValidationException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamMember implements Validatable {

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

    @Override
    public void validate() throws ValidationException {
        if (this.name == null || this.name.isBlank()) {
            throw new ValidationException("name", "NOT_BLANK");
        }

        if (this.githubId == null) {
            throw new ValidationException("githubId", "NOT_NULL");
        }

        if (this.departmentId == null) {
            throw new ValidationException("departmentId", "NOT_NULL");
        }

        if (this.joined == null) {
            throw new ValidationException("joined", "NOT_NULL");
        }
    }
}
