package net.cryptic_game.backend.base.daemon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public final class DaemonEndpointData extends ApiEndpointData {

    private Daemon daemon;

    public DaemonEndpointData(@NotNull final String id, @NotNull final Set<Group> groups, @NotNull final String description, @NotNull final List<ApiParameterData> parameters, final boolean enabled, @NotNull final Object instance, @NotNull final Class<?> clazz, @NotNull final Method method) {
        super(id, groups, description, parameters, enabled, instance, clazz, method, true);
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
