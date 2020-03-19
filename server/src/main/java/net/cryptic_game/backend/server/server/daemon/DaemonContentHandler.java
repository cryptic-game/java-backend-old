package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.cryptic_game.backend.base.utils.JsonSocketUtils.build;
import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getString;

public class DaemonContentHandler extends NettyHandler<JsonObject> {

    private static final Logger log = LoggerFactory.getLogger(DaemonContentHandler.class);
    private final DaemonHandler daemonHandler;

    private final Map<String, DaemonEndpoint> endpoints;

    public DaemonContentHandler(final DaemonHandler daemonHandler, final Map<String, DaemonEndpoint> endpoints) {
        this.daemonHandler = daemonHandler;
        this.endpoints = endpoints;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.daemonHandler.addDaemon(new Daemon(ctx));
        log.info("Daemon connected: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.daemonHandler.removeDaemon(ctx);
        log.info("Daemon disconnected: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject msg) throws Exception {
        String endpointName = getString(msg, "endpoint");
        JsonObject data = getJsonObject(msg, "data");
        if (endpointName == null) {
            ctx.write(build(ResponseType.BAD_REQUEST, "MISSING_ENDPOINT"));
            return;
        }

        if (data == null) {
            ctx.write(build(ResponseType.BAD_REQUEST, "MISSING_DATA"));
            return;
        }

        DaemonEndpoint endpoint = this.endpoints.get(endpointName);
        if (endpoint == null) {
            ctx.write(build(ResponseType.NOT_FOUND, "UNKNOWN_ENDPOINT"));
            return;
        }

        ctx.write(endpoint.handleRequest(data));
    }
}
