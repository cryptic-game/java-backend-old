package net.cryptic_game.backend.daemon.client.daemon;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonClientChannelHandler extends NettyChannelHandler<JsonElement> {

    private static final Logger log = LoggerFactory.getLogger(DaemonClientChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        log.info(msg.toString());
    }
}
