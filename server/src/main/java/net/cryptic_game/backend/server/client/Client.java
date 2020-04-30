package net.cryptic_game.backend.server.client;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.data.user.User;

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
        session.setLastActive(LocalDateTime.now());
        session.saveOrUpdate();
        session.getUser().setLast(LocalDateTime.now());
        session.getUser().saveOrUpdate();
    }

    public User getUser() {
        if (session == null) return null;
        return this.session.getUser();
    }

    public void setSession(final User user, final UUID token, final String deviceName) {
        this.session = Session.createSession(user, token, deviceName);
        session.getUser().setLast(LocalDateTime.now());
        session.getUser().saveOrUpdate();
    }

    public void logout() {
        this.session.delete();
        this.session = null;
    }
}
