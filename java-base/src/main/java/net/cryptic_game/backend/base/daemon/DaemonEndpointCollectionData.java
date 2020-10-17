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
                                        final boolean enabled,
                                        @NotNull final ApiType apiType,
                                        @NotNull final Map<String, ApiEndpointData> endpoints) {
        super(id, description, apiType, enabled, endpoints);
    }

//    public DaemonEndpoint(final JsonObject json, final Daemon daemon) throws IllegalArgumentException {
//        if (!(json.has("name") && json.has("arguments"))) {
//            throw new IllegalArgumentException("Missing \"name\" or \"arguments\" property.");
//        }
//
//        this.name = json.get("name").getAsString();
//        this.daemon = daemon;
//        this.arguments = new HashSet<>();
//
//        final JsonArray jsonArguments = json.get("arguments").getAsJsonArray();
//        for (final JsonElement argument : jsonArguments) {
//            if (!argument.isJsonObject()) {
//                throw new IllegalArgumentException("In the Function-Arguments-Array (\"arguments\"-property) is an Object witch is not a JsonObject.");
//            }
//
//            try {
//                this.arguments.add(new FunctionArgument(argument.getAsJsonObject()));
//            } catch (IllegalArgumentException e) {
//                throw new IllegalArgumentException(e.getError(), e);
//            }
//        }
//    }
}
