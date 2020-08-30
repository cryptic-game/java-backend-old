package net.cryptic_game.backend.base.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Getter
@Service
public class EventLoopGroupService {

    private static final boolean EPOLL = Epoll.isAvailable();

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public EventLoopGroupService() {
        this.bossGroup = EPOLL ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        this.workGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    @PreDestroy
    private void preDestroy() {
        if (!this.bossGroup.isShutdown()) this.bossGroup.shutdownGracefully();
        if (!this.workGroup.isShutdown()) this.workGroup.shutdownGracefully();
    }
}

