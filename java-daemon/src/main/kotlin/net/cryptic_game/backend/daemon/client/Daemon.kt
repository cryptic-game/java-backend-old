package net.cryptic_game.backend.daemon.client

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.json.JsonObjectDecoder
import net.cryptic_game.backend.base.netty.JsonMessageCodec
import net.cryptic_game.backend.base.netty.NettyCodec
import net.cryptic_game.backend.base.netty.NettyHandler
import net.cryptic_game.backend.base.netty.NettyInitializer
import org.slf4j.LoggerFactory

class DaemonClientInitializer : NettyInitializer {
    override fun configure(pipeline: ChannelPipeline) {
        pipeline.addLast(JsonObjectDecoder())
        pipeline.addLast(JsonMessageCodec())
        pipeline.addLast(DaemonClientHandler())
    }
}

class DaemonClientHandler : NettyHandler<JsonElement?>() {

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: JsonElement?) {
        val json = JsonObject()
        json.addProperty("test", 1)
        if (msg is JsonObject && msg != json) ctx!!.write(json) else log.info(msg.toString())
    }

    companion object {
        private val log = LoggerFactory.getLogger(DaemonClientHandler::class.java)
    }
}

class DaemonClientCodec : NettyCodec {
    override val initializer = DaemonClientInitializer()
}
