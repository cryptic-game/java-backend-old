package net.cryptic_game.backend.server.api;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.*;
import net.cryptic_game.backend.base.netty.server.ServerResponseType;

import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class ServerApiExecutor extends ApiExecutor {

    public ServerApiExecutor(ApiHandler apiHandler) {
        super(apiHandler);
    }

    @Override
    public JsonObject execute(ApiExecutionData data) throws ApiException {
        if (!(data instanceof ServerApiExecutionData)) throw new ApiException("Provided " + data.getClass().toString()
                + " is not instance of \"" + ServerApiExecutionData.class.toString() + "\".");

        ServerApiExecutionData executionData = (ServerApiExecutionData) data;

        final ApiEndpointExecutor endpoint = this.apiHandler.getEndpointExecutor(executionData.getEndpoint());
        if (endpoint == null) return build(ServerResponseType.NOT_FOUND, "UNKNOWN_ACTION");

        try {
            return endpoint.execute(data);
        } catch (ApiParameterException e) {
            return build(ServerResponseType.BAD_REQUEST, e.getMessage());
        }
    }
}
