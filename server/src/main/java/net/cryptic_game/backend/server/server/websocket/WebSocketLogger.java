package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class WebSocketLogger extends CombinedChannelDuplexHandler<WebSocketLogger.InputLogger, WebSocketLogger.OutputLogger> {

    public WebSocketLogger() {
        init(new InputLogger(), new OutputLogger());
    }

    public static final class InputLogger extends MessageToMessageDecoder<JsonObject> {

        private static final Logger logger = LoggerFactory.getLogger(InputLogger.class);

        @Override
        protected void decode(ChannelHandlerContext ctx, JsonObject msg, List<Object> out) throws Exception {
            logger.info("Recieved from " + ctx.channel().id() + ": " + msg.toString());
            out.add(msg);
        }
    }

    public static final class OutputLogger extends MessageToMessageEncoder<JsonObject> {

        private static final Logger logger = LoggerFactory.getLogger(OutputLogger.class);

        @Override
        protected void encode(ChannelHandlerContext ctx, JsonObject msg, List<Object> out) throws Exception {
            logger.info("Sent to " + ctx.channel().id() + ": " + msg.toString());
            out.add(msg);
        }
    }
}
