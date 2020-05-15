package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.daemon.DaemonEndpointData;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.ApiUtils;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No {@link ApiEndpointCollection}!
 */
public class WebSocketDaemonEndpoints {

    private static final Logger log = LoggerFactory.getLogger(WebSocketDaemonEndpoints.class);
    private final DaemonHandler daemonHandler;

    public WebSocketDaemonEndpoints(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    public ApiResponse send(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                            @ApiParameter(value = "tag", special = ApiParameterSpecialType.TAG) final String tag,
                            @ApiParameter(value = "endpoint", special = ApiParameterSpecialType.ENDPOINT) final ApiEndpointData endpoint,
                            @ApiParameter(value = "data", special = ApiParameterSpecialType.DATA) final JsonObject data) {

        final Session session = client.get(Session.class);

        if (!(session != null && session.isValid())) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_SESSION");
        }

        if (endpoint instanceof DaemonEndpointData) {

            final DaemonEndpointData daemonEndpoint = (DaemonEndpointData) endpoint;

            this.daemonHandler.addWebSocketRespond(ApiUtils.request(daemonEndpoint.getDaemon().getChannel(),
                    endpoint.getName(),
                    JsonBuilder.create(data)
                            .add("user_id", session.getUser().getId())
            ).toString(), tag, client.getChannel());

            App.addTimeout(App.getInstance().getConfig().getResponseTimeout() * 1000, () -> {
                if (this.daemonHandler.isRequestOpen(tag)) {
                    this.daemonHandler.respondToClient(JsonBuilder.create("tag", tag)
                            .add("info", JsonBuilder.create(ApiResponseType.GATEWAY_TIMEOUT)
                                    .add("notification", false)
                                    .build())
                            .build());
                }
            });
        } else {
            log.warn("Method {}.send(...) was executed with wrong endpoint {}.", WebSocketDaemonEndpoints.class.getName(), endpoint.getName());
            return new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }

        return null;
    }
}
