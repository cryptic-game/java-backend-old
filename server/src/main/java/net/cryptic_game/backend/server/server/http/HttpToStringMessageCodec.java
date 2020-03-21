package net.cryptic_game.backend.server.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

public class HttpToStringMessageCodec extends MessageToMessageCodec<FullHttpRequest, ByteBuf> {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final FullHttpRequest msg, final List<Object> out) throws Exception {
        out.add(msg.content());
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, msg));
    }
}
