package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class JsonSocketServerElementCodec extends MessageToMessageCodec<String, JsonElement> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final JsonElement msg, final List<Object> out) throws Exception {
        out.add(msg.toString());
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final String msg, final List<Object> out) throws Exception {
        out.add(JsonParser.parseString(msg));
    }
}
