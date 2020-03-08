package net.cryptic_game.backend.data.network;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.member.MemberWrapper;
import org.hibernate.Session;

public class NetworkWrapper {

  private static final SQLConnection sqlConnection;

  static {
    final AppBootstrap app = AppBootstrap.getInstance();
    sqlConnection = app.getSqlConnection();
  }

  public static Network createNetwork(final String name, final Device owner, final boolean hidden) {
    final LocalDateTime now = LocalDateTime.now();

    final Network network = new Network();
    network.setName(name);
    network.setOwner(owner);
    network.setHidden(hidden);
    network.setCreated(now);

    final Session sqlSession = sqlConnection.openSession();
    sqlSession.beginTransaction();
    sqlSession.save(network);
    sqlSession.getTransaction().commit();
    sqlSession.close();
    return network;
  }

  public static Network getById(final UUID id) {
    final Session sqlSession = sqlConnection.openSession();
    final Network network = sqlSession.find(Network.class, id);
    sqlSession.close();
    return network;
  }

  public static Network getByName(final String name) {
    final Session sqlSession = sqlConnection.openSession();
    final List<Network> networks = sqlSession
        .createQuery("select object (n) from Network as n where n.name = :name", Network.class)
        .setParameter("name", name)
        .getResultList();
    sqlSession.close();

    if (!networks.isEmpty()) {
      return networks.get(0);
    }
    return null;
  }

  private static void updateNetwork(Network network) {
    final Session sqlSession = sqlConnection.openSession();
    sqlSession.beginTransaction();
    sqlSession.update(network);
    sqlSession.getTransaction().commit();
    sqlSession.close();
  }

  public static boolean updateName(final Network network, final String newName) {
    if (getByName(newName) != null) {
      return false;
    }
    network.setName(newName);
    updateNetwork(network);
    return true;
  }

  public static void updateOwner(final Network network, final Device owner) {
    network.setOwner(owner);
    updateNetwork(network);
  }

  public static void updateHidden(final Network network, final boolean hidden) {
    network.setHidden(hidden);
    updateNetwork(network);
  }

  public static void deleteNetwork(final Network network) {
    final Session sqlSession = sqlConnection.openSession();
    sqlSession.beginTransaction();
    sqlSession.delete(network);
    sqlSession.getTransaction().commit();
    sqlSession.close();
  }

  public static void addDevice(final Network network, final Device device) {
    MemberWrapper.createMembership(network, device);
  }

  public static boolean areDevicesConnected(Device device1, Device device2) {
    try (final Session sqlSession = sqlConnection.openSession()) {
      return sqlSession
          .createQuery("select object (n) from Network n, Member m1 where m1.key.network in " +
                  "(select m2.key.network from Member m2 where m2.key.device = :device2) and m1.key.device = :device1 and n = m1.key.network",
              Network.class)
          .setParameter("device1", device1)
          .setParameter("device2", device2)
          .getResultList().size() > 0;
    }
  }
}
