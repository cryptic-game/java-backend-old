package net.cryptic_game.backend.base.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;
import java.util.function.Consumer;

public class NettyClient {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final String name;
    private final SocketAddress address;
    private final boolean unixSocket;

    private final EventLoopGroupHandler eventLoopGroupHandler;
    private final List<NettyCodecInitializer<?>> nettyInitializers;

    private final Consumer<Channel> connectCallback;

    private Channel channel;

    public NettyClient(final String name, final SocketAddress address, final boolean unixSocket, final EventLoopGroupHandler eventLoopGroupHandler, final NettyCodec<?> nettyCodec, final Consumer<Channel> connectCallback) {
        this.name = name;
        this.address = address;
        this.unixSocket = unixSocket;

        this.eventLoopGroupHandler = eventLoopGroupHandler;
        this.nettyInitializers = nettyCodec.getInitializers();

        this.connectCallback = connectCallback;
    }

    protected void stop() {
        if (this.channel != null) this.channel.close();
    }

    public void start() {
        if ((this.channel == null || !this.channel.isOpen()))
            new Thread(() -> {
                try {
                    this.channel = new Bootstrap()
                            .group(this.eventLoopGroupHandler.getWorkGroup())
                            .channel(this.unixSocket ? (EPOLL ? EpollDomainSocketChannel.class : KQueueDomainSocketChannel.class) :
                                    (EPOLL ? EpollSocketChannel.class : NioSocketChannel.class))
                            .handler(new NettyInitializer(null, this.nettyInitializers))
                            .connect(this.address)
                            .sync()
                            .channel();

                    log.info("The client \"" + this.getName() + "\" is now connected to " + this.address + ".");
                    this.connectCallback.accept(this.channel);

                    this.channel.closeFuture().sync();
                } catch (Exception e) {
                    log.error("The client \"{}\" was unexpectedly closed. {}", this.getName(), e.toString());
                }
                log.info("Reconnecting in 20 seconds.");
                try {
                    Thread.sleep(1000 * 20); // 20 seconds
                } catch (InterruptedException ignored) {
                }
                this.reconnect();
            }, this.getName()).start();
    }

    public void reconnect() {
        this.stop();
        this.start();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public String getName() {
        return this.name;
    }
}
