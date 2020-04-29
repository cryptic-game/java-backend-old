package net.cryptic_game.backend.base.api.netty;

import io.netty.channel.ChannelPipeline;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonMessageCodec;
import net.cryptic_game.backend.base.netty.codec.MessageLoggerCodec;

public class JsonApiServerCodecInitializer extends NettyCodecInitializer<JsonApiServerCodec> {

    @Override
    public void configure(final ChannelPipeline pipeline) {
        if (!AppBootstrap.getInstance().getConfig().isProductive()) {
            pipeline.addLast(new MessageLoggerCodec());
        }
        pipeline.addLast("json-codec", new JsonMessageCodec());
        pipeline.addLast("json-api-handler", new JsonApiServerContentHandler(this.getCodec().getFinder(), this.getCodec().getClientList(), this.getCodec().getConsumer()));
    }
}
