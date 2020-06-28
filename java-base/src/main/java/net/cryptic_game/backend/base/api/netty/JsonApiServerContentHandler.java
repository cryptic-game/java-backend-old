package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiExecutor;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class JsonApiServerContentHandler extends NettyChannelHandler<JsonObject> {

    private final Map<String, ApiEndpointData> endpoints;
    private final Set<ApiClient> clients;
    private final BiConsumer<JsonObject, ApiClient> consumer;
    private final Consumer<ApiClient> connectedCallback;
    private final Consumer<ApiClient> disconnectedCallback;

    public JsonApiServerContentHandler(final Map<String, ApiEndpointData> endpoints,
                                       final Set<ApiClient> clients,
                                       final BiConsumer<JsonObject, ApiClient> consumer,
                                       final Consumer<ApiClient> connectedCallback,
                                       final Consumer<ApiClient> disconnectedCallback) {
        this.endpoints = endpoints;
        this.clients = clients;
        this.consumer = consumer;
        this.connectedCallback = connectedCallback;
        this.disconnectedCallback = disconnectedCallback;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        final ApiClient client = new ApiClient(ctx.channel());
        this.clients.add(client);
        if (this.connectedCallback != null) this.connectedCallback.accept(client);
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        ApiClient client = this.clients.stream().filter((c) -> c.getChannel().equals(ctx.channel())).findFirst().orElse(null);
        if (client == null) return;
        boolean exception;
        do {
            exception = false;
            try {
                this.clients.remove(client);
            } catch (ConcurrentModificationException ignored) {
                exception = true;
            }
            Thread.sleep(1);
        } while (exception);
        if (this.disconnectedCallback != null) this.disconnectedCallback.accept(client);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject msg) throws Exception {
        channelRead0(ctx, msg, null);
    }

    @SuppressWarnings("checkstyle:FinalParameters")
    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject request, final ApiResponse apiResponse) {
        final String tag = JsonUtils.fromJson(request.get("tag"), String.class);

        ApiResponse response = apiResponse;

        if (response == null) {
            ApiClient client = null;
            for (final ApiClient currentClient : this.clients)
                if (currentClient.getChannel().equals(ctx.channel())) client = currentClient;

            if (request.has("response") && request.get("response").getAsBoolean()) {
                if (this.consumer != null) this.consumer.accept(request, client);
                return;
            }

            if (tag == null) response = new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_TAG");
            else response = ApiExecutor.execute(this.endpoints, request, client, tag);
        }

        // TODO Write this in a Timeout with the TAG and the DAEMON and some other information,
        //      to handle the incoming response from the daemon.

        ctx.write(JsonBuilder.create("tag", tag == null ? "00000000-0000-0000-0000-000000000000" : tag)
                .add("info", JsonBuilder.create(response.getType().serialize(true))
                        .add("message", response.hasMessage(), response::getMessage))
                .add("response", true)
                .add("data", response.hasData(), response::getData)
                .build());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause.getCause() instanceof JsonSyntaxException) {
            this.channelRead0(ctx, new JsonObject(), new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_JSON"));
        } else {
            super.exceptionCaught(ctx, cause);
            this.channelRead0(ctx, new JsonObject(), new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR));
        }
    }
}
