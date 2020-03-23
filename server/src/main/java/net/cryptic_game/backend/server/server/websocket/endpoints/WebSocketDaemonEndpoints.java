package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class WebSocketDaemonEndpoints extends ApiEndpointCollection {

    private final DaemonHandler daemonHandler;

    public WebSocketDaemonEndpoints(final DaemonHandler daemonHandler) {
        super("daemon");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("send")
    public ApiResponse send(final ApiClient client,
                            @ApiParameter("function_name") final String functionName,
                            @ApiParameter(value = "data", optional = true) final JsonObject data) {

        final Session session = client.get(Session.class);

        if (!(session != null && session.isValid())) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_SESSION");
        }

        final Function function = this.daemonHandler.getFunction(functionName);

        if (function == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "FUNCTION");
        }

        if (data != null && function.validateArguments(data)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PARAMETERS");
        }

        this.daemonHandler.executeFunction(function, session.getUser().getId(), data == null ? new JsonObject() : data);

        return null;
    }
}
