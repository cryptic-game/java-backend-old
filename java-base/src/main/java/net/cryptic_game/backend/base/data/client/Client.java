package net.cryptic_game.backend.base.data.client;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.data.session.Session;
import net.cryptic_game.backend.base.data.session.SessionWrapper;
import net.cryptic_game.backend.base.data.user.User;

public class Client {

    private final ChannelHandlerContext ctx;
    private final Session session;

    public Client(final ChannelHandlerContext ctx, final User user, final String deviceName) {
        this.ctx = ctx;
        this.session = SessionWrapper.openSession(user, deviceName);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }

    public Session getSession() {
        return session;
    }

    public User getUser() {
        return this.session.getUser();
    }
}
