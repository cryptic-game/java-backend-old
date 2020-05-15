package net.cryptic_game.backend.base.daemon;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterData;

import java.lang.reflect.Method;
import java.util.List;

public class DaemonEndpointData extends ApiEndpointData {

    private Daemon daemon;

    public DaemonEndpointData(final String name, final String description, final Method method, final Object object, final List<ApiParameterData> parameters) {
        super(name, description, method, object, parameters);
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
//                throw new IllegalArgumentException(e.getMessage(), e);
//            }
//        }
//    }

    public Daemon getDaemon() {
        return this.daemon;
    }

    public void setDaemon(final Daemon daemon) {
        this.daemon = daemon;
    }
}
