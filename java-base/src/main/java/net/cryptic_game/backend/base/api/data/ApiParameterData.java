package net.cryptic_game.backend.base.api.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonTransient;
import org.jetbrains.annotations.NotNull;

@Data
@Setter(AccessLevel.NONE)
public final class ApiParameterData {

    @NotNull
    private final String id;
    private final boolean required;
    @JsonTransient
    @NotNull
    private final ApiParameterType type;
    @NotNull
    private final String description;
    @JsonTransient
    @NotNull
    private final Class<?> classType;
}
