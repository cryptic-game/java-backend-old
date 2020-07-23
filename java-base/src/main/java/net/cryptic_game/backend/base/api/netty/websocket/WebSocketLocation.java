package net.cryptic_game.backend.base.api.netty.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.EqualsAndHashCode;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiExecutor;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

@EqualsAndHashCode
public final class WebSocketLocation extends HttpLocation<WebSocketFrame> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Map<String, ApiEndpointData> endpoints;
    private final Consumer<ApiClient> clientAddedConsumer;
    private ApiClient client;

    public WebSocketLocation(final Map<String, ApiEndpointData> endpoints, final Consumer<ApiClient> clientAddedConsumer) {
        super(new HttpObjectAggregator(65536), new WebSocketServerProtocolHandler("/"));
        this.endpoints = endpoints;
        this.clientAddedConsumer = clientAddedConsumer;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame msg) throws Exception {
        if (client == null) {
            this.client = new ApiClient(ctx.channel());
            this.clientAddedConsumer.accept(this.client);
        }

        JsonObject json = null;
        ApiResponse apiResponse = null;

        try {
            json = JsonUtils.fromJson(JsonParser.parseString(msg.content().toString(CHARSET)), JsonObject.class);
        } catch (JsonSyntaxException e) {
            apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "JSON_SYNTAX");
        } catch (JsonTypeMappingException e) {
            apiResponse = e.getCause() instanceof JsonSyntaxException
                    ? new ApiResponse(ApiResponseType.BAD_REQUEST, "JSON_SYNTAX")
                    : new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }

        if (apiResponse == null) {
            final String tag = JsonUtils.fromJson(json.get("tag"), String.class);
            final String endpoint = JsonUtils.fromJson(json.get("endpoint"), String.class);
            final JsonObject data = JsonUtils.fromJson(json.get("data"), JsonObject.class);

            if (tag == null) apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_TAG");
            else if (endpoint == null) apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_ENDPOINT");
            else apiResponse = ApiExecutor.execute(this.endpoints, endpoint, data == null ? new JsonObject() : data, this.client, tag);

            if (apiResponse != null) apiResponse.setTag(tag);
        }

        if (apiResponse != null) {
            ctx.write(new TextWebSocketFrame(Unpooled.copiedBuffer(apiResponse.serialize().toString(), CHARSET)));
        }
    }
}
