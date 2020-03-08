package net.cryptic_game.backend.base.netty

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.MessageToMessageCodec
import io.netty.handler.ssl.SslContext
import org.slf4j.LoggerFactory

interface NettyInitializer {
    fun configure(pipeline: ChannelPipeline)
}

interface NettyCodec {
    val initializer: NettyInitializer
}

abstract class NettyHandler<T> : SimpleChannelInboundHandler<T>() {
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) =
            log.error("Failed to progress channel. \"" + ctx.channel() + "\"", cause)

    companion object {
        private val log = LoggerFactory.getLogger(NettyHandler::class.java)
    }
}

class NettyCodecInitializer(private val sslContext: SslContext?, private val nettyInitializer: NettyInitializer) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(channel: SocketChannel) {
        val pipeline = channel.pipeline()
        if (sslContext != null) pipeline.addLast("ssl", sslContext.newHandler(channel.alloc()))
        nettyInitializer.configure(pipeline)
    }

}

class JsonMessageCodec : MessageToMessageCodec<String?, JsonElement>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, msg: JsonElement, out: MutableList<Any>) {
        out.add(msg.toString())
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, msg: String?, out: MutableList<Any>) {
        out.add(JsonParser.parseString(msg))
    }
}
