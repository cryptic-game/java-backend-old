package net.cryptic_game.backend.server.client;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.data.session.Session;
import net.cryptic_game.backend.base.data.session.SessionWrapper;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.data.user.UserWrapper;

public class Client {

    private final ChannelHandlerContext ctx;
    private Session session;

    public Client(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }

    public Session getSession() {
        return session;
    }

    public User getUser() {
        if (session == null) return null;
        return this.session.getUser();
    }

    public void setSession(final Session session) {
        this.session = session;
        SessionWrapper.setLastToCurrentTime(session);
        UserWrapper.setLastToCurrentTime(session.getUser());
    }

    public void setSession(final User user, final String deviceName) {
        this.session = SessionWrapper.openSession(user, deviceName);
        UserWrapper.setLastToCurrentTime(session.getUser());
    }
}
