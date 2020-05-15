package net.cryptic_game.backend.base.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;

public class NettyServer {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final String name;
    private final SocketAddress address;
    private final boolean unixSocket;

    private final EventLoopGroupHandler eventLoopGroupHandler;
    private final List<NettyCodecInitializer<?>> nettyInitializers;

    private Channel channel;

    public NettyServer(final String name, SocketAddress address, final boolean unixSocket, final EventLoopGroupHandler eventLoopGroupHandler, final NettyCodec<?> nettyCodec) {
        this.name = name;
        this.address = address;
        this.unixSocket = unixSocket;

        this.eventLoopGroupHandler = eventLoopGroupHandler;
        this.nettyInitializers = nettyCodec.getInitializers();
    }

    protected void stop() {
        if (this.channel != null) this.channel.close();
    }

    public void start() {
        if ((this.channel == null || !this.channel.isOpen()))
            new Thread(() -> {
                try {
                    this.channel = new ServerBootstrap()
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .group(this.eventLoopGroupHandler.getBossGroup(), this.eventLoopGroupHandler.getWorkGroup())
                            .channel(this.unixSocket ? (EPOLL ? EpollServerDomainSocketChannel.class : KQueueServerDomainSocketChannel.class) :
                                    (EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class))
                            .childHandler(new NettyInitializer(null, this.nettyInitializers))
                            .bind(this.address)
                            .sync()
                            .channel();

                    log.info("This Server \"" + this.getName() + "\" is now listening on " + this.address + ".");

                    this.channel.closeFuture().sync();
                } catch (Exception e) {
                    log.error("The server \"{}\" was unexpectedly closed. {}", this.getName(), e.toString());
                }
                log.info("Restarting in 20 seconds.");
                try {
                    Thread.sleep(1000 * 20); // 20 seconds
                } catch (InterruptedException ignored) {
                }
                this.restart();
            }, this.getName()).start();
    }

    private void restart() {
        this.stop();
        this.start();
    }

    public String getName() {
        return this.name;
    }
}
