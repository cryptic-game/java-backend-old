package net.cryptic_game.backend.internal.device;

import data.user.User;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.internal.device.access.DeviceAccessWrapper;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceWrapper {

    private static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    public static DeviceImpl getDeviceById(final UUID id) {
        Session sqlSession = sqlConnection.openSession();
        DeviceImpl device = sqlSession.find(DeviceImpl.class, id);
        sqlSession.close();
        return device;
    }

    public static List<User> getCoOwnersOfDevice(final DeviceImpl device) {
        List<User> users = new ArrayList<>();
        DeviceAccessWrapper.getAccessesToDevice(device).forEach((deviceAccess -> users.add(deviceAccess.getUser())));
        return users;
    }

    public static boolean hasUserAccessPermissions(final DeviceImpl device, final User user) {
        return device.getOwner().equals(user) || getCoOwnersOfDevice(device).contains(user);
    }

    public static DeviceImpl createDevice(String name, User owner, boolean poweredOn) {
        final Session sqlSession = sqlConnection.openSession();

        final DeviceImpl device = new DeviceImpl();
        device.setName(name);
        device.setOwner(owner);
        device.setPoweredOn(poweredOn);

        sqlSession.beginTransaction();
        sqlSession.save(device);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return device;
    }

    public static DeviceImpl createDevice(String name, User owner) {
        return createDevice(name, owner, true);
    }
}
