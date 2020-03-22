package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

import java.util.UUID;

public class WebSocketDaemonEndpoints extends ApiEndpointCollection {

    private final DaemonHandler daemonHandler;

    public WebSocketDaemonEndpoints(final DaemonHandler daemonHandler) {
        super("daemon");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("send")
    public ApiResponse send(final ApiClient client,
                            @ApiParameter("session_id") final UUID sessionId,
                            @ApiParameter("session_token") final UUID sessionToken,
                            @ApiParameter("functionName") final String functionName,
                            @ApiParameter(value = "data", optional = true) final JsonObject data) {

        final Session session = Session.getById(sessionId);
        if (!(session != null && session.getToken().equals(sessionToken) && session.isValid())) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_SESSION");
        }

        final Function function = this.daemonHandler.getFunction(functionName);

        if (function == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "FUNCTION");
        }

        if (data != null) if (function.validateArguments(data)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PARAMETERS");
        }

        this.daemonHandler.executeFunction(function, session.getUser(), data);

        return null;
    }
}
