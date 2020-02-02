package net.cryptic_game.backend.base.data.session;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.SQLConnection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionWrapper {

    private static final SQLConnection sqlConnection;
    private static final Duration TIMEOUT = Duration.of(2, ChronoUnit.DAYS);

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    public static Session openSession(final User user, final String deviceName) {
        final Session session = new Session();
        session.setUser(user);
        session.setDeviceName(deviceName);
        session.setExpire(LocalDateTime.now().plus(TIMEOUT));

        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();

        return session;
    }

    public static boolean isValide(final Session session) {
        return session.isValid() && LocalDateTime.now().isBefore(session.getExpire());
    }
}
