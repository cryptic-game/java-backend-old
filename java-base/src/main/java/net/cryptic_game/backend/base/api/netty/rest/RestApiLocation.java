package net.cryptic_game.backend.base.api.netty.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiExecutor;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocation;
import net.cryptic_game.backend.base.utils.HttpUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class RestApiLocation extends HttpLocation<FullHttpRequest> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Map<String, ApiEndpointData> endpoints;
    private final boolean useApiToken;
    private final String apiToken;

    public RestApiLocation(final Map<String, ApiEndpointData> endpoints, final String apiToken) {
        super(new HttpObjectAggregator(65536));
        this.endpoints = endpoints;
        this.useApiToken = apiToken != null && !apiToken.isBlank();
        this.apiToken = apiToken;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest msg) throws Exception {
        if (this.useApiToken) {
            final String givenApiToken = msg.headers().get(HttpHeaderNames.AUTHORIZATION);
            if (givenApiToken == null || !givenApiToken.equals(this.apiToken)) {
                HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.UNAUTHORIZED);
                return;
            }
        }

        if (!(msg.method().equals(HttpMethod.POST) || msg.method().equals(HttpMethod.GET))) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String content = msg.content().toString(CHARSET);

        JsonObject json = null;
        ApiResponse apiResponse = null;

        try {
            json = content.isEmpty() ? new JsonObject() : JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class);
        } catch (JsonSyntaxException e) {
            apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "JSON_SYNTAX");
        } catch (JsonTypeMappingException e) {
            apiResponse = e.getCause() instanceof JsonSyntaxException
                    ? new ApiResponse(ApiResponseType.BAD_REQUEST, "JSON_SYNTAX")
                    : new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }

        if (apiResponse == null) {
            apiResponse = ApiExecutor.execute(this.endpoints, new URI(msg.uri()).getPath().substring(1), json == null ? new JsonObject() : json, null, null);
        }

        if (apiResponse != null) {
            final HttpResponse httpResponse = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.valueOf(
                    apiResponse.getResponseType().getCode(),
                    apiResponse.getResponseType().getDisplayName()
            ));
            HttpUtils.setContentTypeHeader(httpResponse, HttpHeaderValues.APPLICATION_JSON, CHARSET);

            HttpUtils.sendAndCleanupConnection(ctx, msg, httpResponse,
                    new DefaultLastHttpContent(Unpooled.copiedBuffer(apiResponse.serialize(true).toString(), CHARSET)));
        }
    }
}
