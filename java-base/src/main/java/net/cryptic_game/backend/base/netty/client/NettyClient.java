package net.cryptic_game.backend.base.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.base.netty.codec.NettyCodecHandler;

import java.net.SocketAddress;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class NettyClient implements AutoCloseable {

    private static final boolean EPOLL = Epoll.isAvailable();

    private final String id;
    private final SocketAddress address;
    private final NettyCodecHandler codecHandler;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    private final Consumer<Channel> connectCallback;

    private Channel channel;
    private Thread thread;

    protected abstract Class<? extends Channel> getChannelType(boolean epoll);

    public final void start() {
        this.close();
        this.thread = new Thread(() -> {
            try {
                this.channel = new Bootstrap()
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .group(this.eventLoopGroupHandler.getWorkGroup())
                        .channel(this.getChannelType(EPOLL))
                        .handler(new NettyInitializer(null, this.codecHandler.getInitializers()))
                        .connect(this.address)
                        .sync()
                        .channel();

                log.info("The client {} is now connected to {}.", this.id, this.address);
                this.connectCallback.accept(this.channel);

                this.channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("The client {} was unexpectedly closed. {}", this.id, e.toString());
            }
            log.info("Reconnecting in 20 seconds.");
            try {
                Thread.sleep(1000 * 20); // 20 seconds
            } catch (InterruptedException ignored) {
            }
            this.start();
        }, "client-" + this.id);
        this.thread.start();
    }

    /**
     * Closes the current client.
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
