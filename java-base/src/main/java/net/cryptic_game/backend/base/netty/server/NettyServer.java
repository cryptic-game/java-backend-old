package net.cryptic_game.backend.base.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.netty.EventLoopGroupService;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.SocketAddress;

@Slf4j
@EqualsAndHashCode
@RequiredArgsConstructor
public final class NettyServer implements AutoCloseable {

    private static final boolean EPOLL = Epoll.isAvailable();
    private final String id;
    private final SocketAddress address;
    private final SslContext sslContext;
    private final NettyCodecHandler codecHandler;
    private final EventLoopGroupService eventLoopGroupService;

    private Thread thread;
    private Channel channel;

    protected Class<? extends ServerChannel> getServerChannelType() {
        return EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    /**
     * Starts the current server.
     */
    public void start() {
        this.close();
        this.thread = new Thread(() -> {
            try {
                this.channel = new ServerBootstrap()
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .group(this.eventLoopGroupService.getBossGroup(), this.eventLoopGroupService.getWorkGroup())
                        .channel(this.getServerChannelType())
                        .childHandler(new NettyInitializer(this.sslContext, this.codecHandler.getInitializers()))
                        .bind(this.address)
                        .sync()
                        .channel();

                log.info("Server {} is now listening on {}.", this.id, this.channel.localAddress());

                this.channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("Server " + this.id + " crashed. Restarting in 20 seconds.", e);

                try {
                    Thread.sleep(20000);
                } catch (InterruptedException ignored) {
                }
                this.start();

            } finally {
                this.close();
            }
        }, "server-" + this.id);
        this.thread.start();
    }

    /**
     * Closes the current server.
     */
    @Override
    public void close() {
        if (this.channel != null) {
            if (this.channel.isOpen()) this.channel.close();
            this.channel = null;
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }
}
