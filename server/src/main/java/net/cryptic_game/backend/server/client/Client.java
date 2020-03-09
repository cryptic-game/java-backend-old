package net.cryptic_game.backend.server.client;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.data.User;
import net.cryptic_game.backend.data.user.session.Session;
import net.cryptic_game.backend.data.user.session.SessionWrapper;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public void setSession(final Session session) {
        this.session = session;
        SessionWrapper.setLastToCurrentTime(session);
        session.getUser().setLast(LocalDateTime.now());
        session.getUser().update();
    }

    public User getUser() {
        if (session == null) return null;
        return this.session.getUser();
    }

    public void setSession(final User user, final UUID token, final String deviceName) {
        this.session = SessionWrapper.openSession(user, token, deviceName);
        session.getUser().setLast(LocalDateTime.now());
        session.getUser().update();
    }

    public void logout() {
        SessionWrapper.closeSession(this.session);
        this.session = null;
    }
}
