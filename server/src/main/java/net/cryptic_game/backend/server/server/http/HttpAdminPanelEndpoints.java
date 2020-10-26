package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiService;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@ApiEndpointCollection(id = "admin_panel", description = "Endpoints for the admin panel", type = ApiType.REST, authenticator = HttpServerAuthenticator.class)
public final class HttpAdminPanelEndpoints {

    private final Bootstrap bootstrap;
    private WebsocketApiService websocketApiService = null;

    @ApiEndpoint(id = "endpoints")
    public ApiResponse getEndpoints() {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        Map<String, JsonElement> outputMap = new HashMap<>();
        websocketApiService.getEndpoints().forEach((key, value) -> outputMap.put(key, value.serialize()));
        return new ApiResponse(HttpResponseStatus.OK, outputMap);
    }

    @ApiEndpoint(id = "enable")
    public ApiResponse enableEndpoint(@ApiParameter(id = "name") final String name) {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        ApiEndpointData endpointData = websocketApiService.getEndpoints().get(name);
        endpointData.setDisabled(false);
        return new ApiResponse(HttpResponseStatus.OK, endpointData);
    }

    @ApiEndpoint(id = "disable")
    public ApiResponse disableEndpoint(@ApiParameter(id = "name") final String name) {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        ApiEndpointData endpointData = websocketApiService.getEndpoints().get(name);
        endpointData.setDisabled(true);
        return new ApiResponse(HttpResponseStatus.OK, endpointData);
    }

}
