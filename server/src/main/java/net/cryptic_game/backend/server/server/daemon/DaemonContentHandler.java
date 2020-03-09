package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class DaemonContentHandler extends NettyHandler<JsonObject> {

    private final DaemonHandler daemonHandler;

    public DaemonContentHandler(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject msg)
            throws Exception {
        ctx.write(msg);
    }
}
