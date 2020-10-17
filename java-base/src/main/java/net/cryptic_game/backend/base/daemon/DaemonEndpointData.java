package net.cryptic_game.backend.base.daemon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;

import java.lang.reflect.Method;

@Getter
@Setter
@EqualsAndHashCode
public final class DaemonEndpointData extends ApiEndpointData {

    private Daemon daemon;

    public DaemonEndpointData(final String id, final String description, final ApiParameterData[] parameters, final int authentication, final boolean enabled, final ApiAuthenticator authenticator, final Object instance, final Class<?> clazz, final Method method, final Daemon daemon) {
        super(id, description, parameters, authentication, enabled, authenticator, instance, clazz, method);
        this.daemon = daemon;
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
