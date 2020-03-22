package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class WebSocketDaemonEndpoints extends ApiEndpointCollection {

    private final DaemonHandler daemonHandler;

    public WebSocketDaemonEndpoints(final DaemonHandler daemonHandler) {
        super("daemon");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("send")
    public ApiResponse send(final ApiClient client,
                            @ApiParameter("functionName") final String functionName,
                            @ApiParameter("data") final JsonObject data) {

        final Function function = this.daemonHandler.getFunction(functionName);

        if (function == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "FUNCTION");
        }

        if (function.validateArguments(data)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PARAMETERS");
        }


        return new ApiResponse(ApiResponseType.OK);
    }
}
