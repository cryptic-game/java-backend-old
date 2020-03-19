package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.server.daemon.Daemon;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonContentHandler extends NettyHandler<JsonObject> {

    private static final Logger log = LoggerFactory.getLogger(DaemonContentHandler.class);
    private final DaemonHandler daemonHandler;

    public DaemonContentHandler(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
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
        log.info(msg.toString());
        ctx.write(msg);
    }
}
