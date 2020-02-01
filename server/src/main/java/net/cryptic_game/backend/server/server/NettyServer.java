package net.cryptic_game.backend.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final String name;
    private final InetSocketAddress inetAddress;

    private final EventLoopGropHandler eventLoopGropHandler;
    private final ServerCodecInitializer serverCodecInitializer;

    private Channel channel;

    public NettyServer(final String name, final String host, final int port, final EventLoopGropHandler eventLoopGropHandler, final ServerCodec serverCodec) {
        this.name = name;
        this.inetAddress = new InetSocketAddress(host, port);

        this.eventLoopGropHandler = eventLoopGropHandler;
        this.serverCodecInitializer = serverCodec.getInitializer();
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
                            .group(this.eventLoopGropHandler.getBossGroup(), this.eventLoopGropHandler.getWorkGroup())
                            .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                            .childHandler(new NettyServerInitializer(null, this.serverCodecInitializer))
                            .bind(this.inetAddress)
                            .sync()
                            .channel();

                    log.info("This Server is now listening on port " + this.inetAddress.getPort() + ".");

                    this.channel.closeFuture().sync();
                } catch (IllegalArgumentException | InterruptedException e) {
                    log.error("The server was closed unexpectedly.", e);
                } finally {
                    this.stop();
                }
            }, this.getName()).start();
    }

    public String getName() {
        return this.name;
    }
}
