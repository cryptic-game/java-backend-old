package net.cryptic_game.backend.base.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopGropHandler {

    private static final boolean EPOLL = Epoll.isAvailable();

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public EventLoopGropHandler() {
        this.bossGroup = EPOLL ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        this.workGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    private void shutdown() {
        if (!this.bossGroup.isShutdown()) this.bossGroup.shutdownGracefully();
        if (!this.workGroup.isShutdown()) this.workGroup.shutdownGracefully();
    }

    public EventLoopGroup getBossGroup() {
        return this.bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return this.workGroup;
    }
}

