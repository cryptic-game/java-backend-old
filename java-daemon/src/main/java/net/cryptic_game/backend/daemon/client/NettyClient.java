package net.cryptic_game.backend.daemon.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {

  private static final boolean EPOLL = Epoll.isAvailable();
  private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

  private final String name;
  private final InetSocketAddress inetAddress;

  private final EventLoopGroupHandler eventLoopGroupHandler;
  private final NettyInitializer nettyInitializer;

  private boolean running;
  private Channel channel;

  public NettyClient(final String name, final String host, final int port,
      final EventLoopGroupHandler eventLoopGroupHandler, final NettyCodec nettyCodec) {
    this.name = name;
    this.inetAddress = new InetSocketAddress(host, port);

    this.eventLoopGroupHandler = eventLoopGroupHandler;
    this.nettyInitializer = nettyCodec.getInitializer();

    this.running = false;
  }

  protected void stop() {
    if (this.channel != null) {
      this.channel.close();
    }
  }

  public void start() {
    if ((this.channel == null || !this.channel.isOpen())) {
      new Thread(() -> {
        try {
          this.channel = new Bootstrap()
              .group(this.eventLoopGroupHandler.getWorkGroup())
              .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
              .handler(new NettyCodecInitializer(null, this.nettyInitializer))
              .connect(this.inetAddress)
              .sync()
              .channel();

          log.info("The client \"" + this.getName() + "\" is now connected to " + this.inetAddress
              + ".");

          this.channel.closeFuture().sync();
        } catch (Exception e) {
          log.error("The client \"" + this.getName() + "\" was unexpectedly closed.", e);
        }
        log.info("Reconnecting in 20 seconds.");
        try {
          Thread.sleep(1000 * 20); // 20 seconds
        } catch (InterruptedException ignored) {
        }
        this.reconnect();
      }, this.getName()).start();
    }
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
