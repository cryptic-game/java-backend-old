package net.cryptic_game.backend.server.server.daemon;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class DaemonCodec implements NettyCodec {

  private final DaemonHandler daemonHandler;

  public DaemonCodec(final DaemonHandler daemonHandler) {
    this.daemonHandler = daemonHandler;
  }

  @Override
  public NettyInitializer getInitializer() {
    return new DaemonInitializer(this.daemonHandler);
  }
}
