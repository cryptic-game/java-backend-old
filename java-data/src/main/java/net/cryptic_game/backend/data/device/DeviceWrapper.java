package net.cryptic_game.backend.data.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.data.device.access.DeviceAccessWrapper;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;

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
    DeviceAccessWrapper.getAccessesToDevice(device)
        .forEach((deviceAccess -> users.add(deviceAccess.getUser())));
    return users;
  }

  public static boolean hasUserAccessPermissions(final Device device, final User user) {
    return device.getOwner().equals(user) || getCoOwnersOfDevice(device).contains(user);
  }

  public static Device createDevice(String name, User owner, boolean poweredOn) {
    final Session sqlSession = sqlConnection.openSession();

    final Device device = new Device();
    device.setName(name);
    device.setOwner(owner);
    device.setPoweredOn(poweredOn);

    sqlSession.beginTransaction();
    sqlSession.save(device);
    sqlSession.getTransaction().commit();
    sqlSession.close();
    return device;
  }

  public static Device createDevice(String name, User owner) {
    return createDevice(name, owner, true);
  }
}
