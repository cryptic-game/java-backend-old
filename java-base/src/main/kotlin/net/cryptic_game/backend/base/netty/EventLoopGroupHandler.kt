package net.cryptic_game.backend.base.netty

import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup

class EventLoopGroupHandler {
    val bossGroup: EventLoopGroup = if (EPOLL) EpollEventLoopGroup(1) else NioEventLoopGroup(1)
    val workGroup: EventLoopGroup = if (EPOLL) EpollEventLoopGroup() else NioEventLoopGroup()

    private fun shutdown() {
        if (!bossGroup.isShutdown) bossGroup.shutdownGracefully()
        if (!workGroup.isShutdown) workGroup.shutdownGracefully()
    }

    companion object {
        private val EPOLL = Epoll.isAvailable()
    }

}
