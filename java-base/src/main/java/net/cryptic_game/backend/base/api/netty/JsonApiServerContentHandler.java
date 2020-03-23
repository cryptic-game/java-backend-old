package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClientList;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class JsonApiServerContentHandler extends NettyChannelHandler<JsonObject> {

    private static final Logger log = LoggerFactory.getLogger(JsonApiServerContentHandler.class);
    private final ApiEndpointFinder executor;
    private final ApiClientList clientList;
    private final Consumer<JsonObject> consumer;

    public JsonApiServerContentHandler(final ApiEndpointFinder executor, final ApiClientList clientList, final Consumer<JsonObject> consumer) {
        this.executor = executor;
        this.clientList = clientList;
        this.consumer = consumer;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.clientList.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.clientList.remove(this.clientList.get(ctx.channel()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonObject msg) throws Exception {
        channelRead0(ctx, msg, null);
    }

    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject request, ApiResponse apiResponse) {
        JsonElement tag = null;

        if (apiResponse == null) {
            if (request.has("response") && request.get("response").getAsBoolean()) {
                if (this.consumer != null) consumer.accept(request);
                return;
            }

            if (request.has("tag")) tag = request.get("tag");
            else apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_TAG");

            if (apiResponse == null) if (request.has("endpoint")) {
                final String endpoint = request.get("endpoint").getAsString();
                final JsonObject data = request.has("data") ? request.get("data").getAsJsonObject() : new JsonObject();
                try {
                    apiResponse = this.executor.execute(this.clientList.get(ctx.channel()), tag, endpoint, data);
                } catch (ApiException e) {
                    log.error("Error while executing JsonApi-Endpoint \"" + endpoint + "\".", e);
                    apiResponse = new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
                }
            } else {
                apiResponse = new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_ENDPOINT");
            }
        }

        if (apiResponse != null) {
            final JsonObject response = new JsonObject();
            response.addProperty("tag", tag == null ? "00000000-0000-0000-0000-000000000000" : tag.getAsString());
            final JsonObject info = apiResponse.getType().serialize(true);
            if (apiResponse.hasErrorMessage()) info.addProperty("message", apiResponse.getMessage());
            response.add("info", info);
            response.addProperty("response", true);
            if (apiResponse.getData() != null) response.add("data", apiResponse.getData());

            // TODO: Write this in a Timeout with the TAG and the DAEMON and some other information,
            //       to handle the incoming response from the daemon.

            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getCause() instanceof JsonSyntaxException)
            channelRead0(ctx, new JsonObject(), new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_JSON"));
        else {
            super.exceptionCaught(ctx, cause);
            channelRead0(ctx, new JsonObject(), new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR));
        }
    }
}
