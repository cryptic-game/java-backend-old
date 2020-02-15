package net.cryptic_game.backend.base.data.network;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.data.device.Device;
import net.cryptic_game.backend.base.data.network.member.Member;
import net.cryptic_game.backend.base.data.network.member.MemberWrapper;
import net.cryptic_game.backend.base.sql.SQLConnection;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(network);
        session.getTransaction().commit();
        session.close();
        return network;
    }

    public static Network getById(final UUID id) {
        final Session session = sqlConnection.openSession();
        final Network network = session.find(Network.class, id);
        session.close();
        return network;
    }

    public static Network getByName(final String name) {
        final Session session = sqlConnection.openSession();
        final List<Network> networks = session
                .createQuery("select object (n) from Network as n where n.name = :name", Network.class)
                .setParameter("name", name)
                .getResultList();
        session.close();

        if (!networks.isEmpty()) return networks.get(0);
        return null;
    }

    private static void updateNetwork(Network network) {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.update(network);
        session.getTransaction().commit();
        session.close();
    }

    public static boolean updateName(final Network network, final String newName) {
        if (getByName(newName) != null) return false;
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
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.delete(network);
        session.getTransaction().commit();
        session.close();
    }

    public static Member addDevice(final Network network, final Device device) {
        return MemberWrapper.createMembership(network, device);
    }
}
