package net.cryptic_game.backend.daemon.client

import net.cryptic_game.backend.base.netty.EventLoopGroupHandler
import net.cryptic_game.backend.base.netty.NettyCodec
import java.util.function.Consumer

class NettyClientHandler {
    private val clients: MutableSet<NettyClient> = mutableSetOf()
    private val eventLoopGroupHandler: EventLoopGroupHandler = EventLoopGroupHandler()

    fun addClient(name: String, host: String, port: Int, nettyCodec: NettyCodec) {
        clients.add(NettyClient(name, host, port, eventLoopGroupHandler, nettyCodec))
    }

    fun start() = clients.forEach(Consumer { obj: NettyClient -> obj.start() })

}