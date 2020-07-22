package net.cryptic_game.backend.base.netty.server;

import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.SocketAddress;

public final class NettyDomainServer extends NettyServer {

    public NettyDomainServer(final String id, final SocketAddress address, final SslContext sslContext,
                             final NettyCodecHandler codecHandler, final EventLoopGroupHandler eventLoopGroupHandler) {
        super(id, address, sslContext, codecHandler, eventLoopGroupHandler);
    }

    @Override
    protected Class<? extends ServerChannel> getServerChannelType(final boolean epoll) {
        return epoll ? EpollServerDomainSocketChannel.class : KQueueServerSocketChannel.class;
    }
}
