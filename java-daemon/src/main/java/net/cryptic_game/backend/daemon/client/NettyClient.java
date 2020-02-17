package net.cryptic_game.backend.daemon.client;

import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyClient {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final String name;
    private final InetSocketAddress inetAddress;

    private final EventLoopGroupHandler eventLoopGroupHandler;
    private final NettyInitializer nettyInitializer;

    private Channel channel;

    public NettyClient(final String name, final String host, final int port, final EventLoopGroupHandler eventLoopGroupHandler, final NettyCodec nettyCodec) {
        this.name = name;
        this.inetAddress = new InetSocketAddress(host, port);

        this.eventLoopGroupHandler = eventLoopGroupHandler;
        this.nettyInitializer = nettyCodec.getInitializer();
    }

    protected void stop() {
        if (this.channel != null) this.channel.close();
    }

    public void start() {
//        if ((this.channel == null || !this.channel.isOpen()))
//            new Thread(() -> {
//                try {
//                    this.channel = new ServerBootstrap()
//                            .option(ChannelOption.SO_BACKLOG, 1024)
//                            .group(this.eventLoopGropHandler.getBossGroup(), this.eventLoopGropHandler.getWorkGroup())
//                            .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
//                            .childHandler(new NettyCodecInitializer(null, this.nettyInitializer))
//                            .bind(this.inetAddress)
//                            .sync()
//                            .channel();
//
//                    log.info("This Server is now listening on port " + this.inetAddress.getPort() + ".");
//
//                    this.channel.closeFuture().sync();
//                } catch (IllegalArgumentException | InterruptedException e) {
//                    log.error("The client was unexpectedly closed.", e);
//                } finally {
//                    this.stop();
//                }
//            }, this.getName()).start();
    }

    public String getName() {
        return this.name;
    }
}
