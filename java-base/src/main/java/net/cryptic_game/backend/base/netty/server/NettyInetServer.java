package net.cryptic_game.backend.base.netty.server;

import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.InetSocketAddress;

public final class NettyInetServer extends NettyServer {


    public NettyInetServer(final String id, final InetSocketAddress address, final SslContext sslContext,
                           final NettyCodecHandler codecHandler, final EventLoopGroupHandler eventLoopGroupHandler) {
        super(id, address, sslContext, codecHandler, eventLoopGroupHandler);
    }

    @Override
    protected Class<? extends ServerChannel> getServerChannelType(final boolean epoll) {
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }
}
