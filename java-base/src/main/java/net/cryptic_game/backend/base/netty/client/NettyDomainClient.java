package net.cryptic_game.backend.base.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.SocketAddress;
import java.util.function.Consumer;

public final class NettyDomainClient extends NettyClient {

    public NettyDomainClient(final String id, final SocketAddress address, final NettyCodecHandler codecHandler,
                             final EventLoopGroupHandler eventLoopGroupHandler, final Consumer<Channel> connectCallback) {
        super(id, address, codecHandler, eventLoopGroupHandler, connectCallback);
    }

    @Override
    protected Class<? extends Channel> getChannelType(final boolean epoll) {
        return epoll ? EpollDomainSocketChannel.class : KQueueDomainSocketChannel.class;
    }
}
