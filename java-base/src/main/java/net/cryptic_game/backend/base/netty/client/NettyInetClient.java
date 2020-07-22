package net.cryptic_game.backend.base.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.SocketAddress;
import java.util.function.Consumer;

public final class NettyInetClient extends NettyClient {

    public NettyInetClient(final String id, final SocketAddress address, final NettyCodecHandler codecHandler,
                           final EventLoopGroupHandler eventLoopGroupHandler, final Consumer<Channel> connectCallback) {
        super(id, address, codecHandler, eventLoopGroupHandler, connectCallback);
    }

    @Override
    protected Class<? extends Channel> getChannelType(final boolean epoll) {
        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
