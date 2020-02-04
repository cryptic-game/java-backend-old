package net.cryptic_game.backend.server.server;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class JsonMessageEncoder extends MessageToMessageEncoder<JsonElement> {

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonElement msg, List<Object> out) throws Exception {

    }
}
