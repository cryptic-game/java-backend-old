package net.cryptic_game.backend.data.device;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.data.device.access.DeviceAccessWrapper;
import net.cryptic_game.backend.data.user.User;
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

    public static Device getDeviceById(final UUID id) {
        Session sqlSession = sqlConnection.openSession();
        Device device = sqlSession.find(Device.class, id);
        sqlSession.close();
        return device;
    }

    public static List<User> getCoOwnersOfDevice(final Device device) {
        List<User> users = new ArrayList<>();
        DeviceAccessWrapper.getAccessesToDevice(device).forEach((deviceAccess -> users.add(deviceAccess.getUser())));
        return users;
    }

    public static boolean hasUserAccessPermissions(final Device device, final User user) {
        return device.getOwner().equals(user) || getCoOwnersOfDevice(device).contains(user);
    }
}
