package net.cryptic_game.backend.base.api.netty;

import io.netty.channel.ChannelPipeline;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonLoggerMessageCodec;

public class JsonApiServerCodecInitializer extends NettyCodecInitializer<JsonApiServerCodec> {

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast("json-element-codec", new JsonSocketServerElementCodec());
        if (!AppBootstrap.getInstance().getConfig().getAsBoolean(BaseConfig.PRODUCTIVE)) {
            pipeline.addLast(new JsonLoggerMessageCodec());
        }
        pipeline.addLast("json-api-handler", new JsonApiServerContentHandler(this.getCodec().getFinder(), this.getCodec().getClientList()));
    }
}
