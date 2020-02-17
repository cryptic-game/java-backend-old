package net.cryptic_game.backend.base.data.device.access;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.data.device.Device;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.SQLConnection;
import org.hibernate.Session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DeviceAccessWrapper {

    private static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    public static boolean hasUserAccessToDevice(final User user, final Device device) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.user = :user and a.device = :device and a.valid = true and a.exprie > :currentDate", DeviceAccess.class)
                    .setParameter("user", user)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList().size() > 0;
        }
    }

    public static DeviceAccess grantAccessToDevice(final User user, final Device device, final Duration duration) {
        Session sqlSession = sqlConnection.openSession();

        DeviceAccess access = new DeviceAccess();
        access.setUser(user);
        access.setDevice(device);
        access.setAccessGranted(LocalDateTime.now());
        access.setValid(true);
        access.setExpire(LocalDateTime.now().plus(duration));

        sqlSession.beginTransaction();
        sqlSession.save(access);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return access;
    }

    public static List<DeviceAccess> getAccessesToDevice(final Device device) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.device = :device and a.valid = true and a.exprie > :currentDate", DeviceAccess.class)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList();
        }
    }
}
