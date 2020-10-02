package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@EqualsAndHashCode
public final class ApiResponse {

    @NotNull
    private final ApiResponseStatus responseCode;

    @Nullable
    private final String error;

    @Nullable
    private final JsonElement json;

    @Setter
    @Nullable
    private String tag;

    public ApiResponse(@NotNull final ApiResponseStatus responseCode) {
        this.responseCode = responseCode;
        this.error = null;
        this.json = null;
    }

    public ApiResponse(@NotNull final ApiResponseStatus responseCode, @NotNull final String error) {
        this.responseCode = responseCode;
        this.error = error;
        this.json = null;
    }

    public ApiResponse(@NotNull final ApiResponseStatus responseCode, @NotNull final Object json) {
        this.responseCode = responseCode;
        this.error = null;
        this.json = JsonUtils.toJson(json);
    }
}
