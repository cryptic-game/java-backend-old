package net.cryptic_game.backend.admin.dto.server_management;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class Endpoint {

    private final String id;
    private final String description;
    private final List<Parameter> parameters;
    private final boolean disabled;

    @Nullable
    private String reason;

    public Endpoint(@JsonProperty("id") final String id,
                    @JsonProperty("description") final String description,
                    @JsonProperty("disabled") final boolean disabled,
                    @JsonProperty("parameters") final List<Parameter> parameters) {
        this.id = id;
        this.description = description;
        this.parameters = parameters;
        this.disabled = disabled;
    }

    @Data
    public static class Parameter {
        private String id;
        private boolean required;
        private String description;

        public Parameter(@JsonProperty("id") final String id,
                         @JsonProperty("required") final boolean required,
                         @JsonProperty("description") final String description) {
            this.id = id;
            this.description = description;
            this.required = required;
        }
    }
}
