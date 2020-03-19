package net.cryptic_game.backend.daemon.client.daemon;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonClientHandler extends NettyHandler<JsonElement> {

    private static final Logger log = LoggerFactory.getLogger(DaemonClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        log.info(msg.toString());
    }
}
