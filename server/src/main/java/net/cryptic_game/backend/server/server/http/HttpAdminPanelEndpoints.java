package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.server.api.handler.websocket.WebsocketApiInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ApiEndpointCollection(id = "admin_panel", description = "Endpoints for the admin panel", type = ApiType.REST, authenticator = HttpServerAuthenticator.class)
public final class HttpAdminPanelEndpoints {

    private final ApplicationContext context;
    private WebsocketApiInitializer websocketApiService = null;

    @ApiEndpoint(id = "endpoints")
    public ApiResponse getEndpoints() {
        if (this.websocketApiService == null) this.websocketApiService = this.context.getBean(WebsocketApiInitializer.class);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("endpoints", this.websocketApiService.getEndpoints()));
    }

    @ApiEndpoint(id = "info")
    public ApiResponse getEndpointInfo(@ApiParameter(id = "id") final String id) {
        if (this.websocketApiService == null) this.websocketApiService = this.context.getBean(WebsocketApiInitializer.class);
        final ApiEndpointData endpointData = this.websocketApiService.getEndpoints().get(id);
        if (endpointData == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND");
        }
        return new ApiResponse(HttpResponseStatus.OK, endpointData);
    }

    @ApiEndpoint(id = "enable")
    public ApiResponse enableEndpoint(@ApiParameter(id = "id") final String id) {
        if (this.websocketApiService == null) this.websocketApiService = this.context.getBean(WebsocketApiInitializer.class);
        final ApiEndpointData endpointData = this.websocketApiService.getEndpoints().get(id);
        if (endpointData == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND");
        }
        endpointData.setDisabled(false);
        return new ApiResponse(HttpResponseStatus.OK, endpointData);
    }

    @ApiEndpoint(id = "disable")
    public ApiResponse disableEndpoint(@ApiParameter(id = "id") final String id) {
        if (this.websocketApiService == null) this.websocketApiService = this.context.getBean(WebsocketApiInitializer.class);
        final ApiEndpointData endpointData = this.websocketApiService.getEndpoints().get(id);
        if (endpointData == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND");
        }
        endpointData.setDisabled(true);
        return new ApiResponse(HttpResponseStatus.OK, endpointData);
    }

}
