package net.cryptic_game.backend.base.data.session;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.SQLConnection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class SessionWrapper {

    private static final SQLConnection sqlConnection;
    private static final Duration EXPIRE;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        final Config config = app.getConfig();
        sqlConnection = app.getSqlConnection();
        EXPIRE = Duration.of(config.getAsInt(BaseConfig.SESSION_EXPIRE), ChronoUnit.DAYS);
    }

    public static Session openSession(final User user, final String deviceName) {
        final Session session = new Session();
        session.setUser(user);
        session.setDeviceName(deviceName);
        session.setExpire(LocalDateTime.now().plus(EXPIRE));
        session.setLastActive(LocalDateTime.now());
        session.setValid(true);

        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();

        return session;
    }

    public static boolean isValid(final Session session) {
        return session.isValid() && LocalDateTime.now().isBefore(session.getExpire());
    }

    public static Session getSessionById(UUID id) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        Session session = sqlSession.find(Session.class, id);
        sqlSession.close();

        return session;
    }

    public static void setLastToCurrentTime(Session session) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        session.setLastActive(LocalDateTime.now());
        sqlSession.update(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }
}
