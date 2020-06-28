package net.cryptic_game.backend.data.network;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.data.device.Device;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Entity representing a network member entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_member")
@Data
public class NetworkMember extends TableModel implements JsonSerializable {

    @EmbeddedId
    private MemberKey key;

    /**
     * Creates a new {@link NetworkMember}.
     *
     * @param network {@link Network} of the {@link NetworkMember}
     * @param device  {@link Device} of the {@link NetworkMember}
     * @return The instance of the created {@link NetworkMember}
     */
    public static NetworkMember createMember(final Network network, final Device device) {
        NetworkMember existingMember = getMember(network, device);
        if (existingMember != null) return existingMember;

        final NetworkMember networkMember = new NetworkMember();
        networkMember.setNetwork(network);
        networkMember.setDevice(device);

        final Session sqlSession = SQL_CONNECTION.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(networkMember);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return networkMember;
    }

    /**
     * Fetches the {@link NetworkMember} with the given key.
     *
     * @param network {@link Network} of the {@link NetworkMember}
     * @param device  {@link Device} of the {@link NetworkMember}
     * @return The instance of the fetched {@link NetworkMember} if it exists | null if the entity does not exist
     */
    public static NetworkMember getMember(final Network network, final Device device) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            final MemberKey primaryKey = new MemberKey();
            primaryKey.setNetwork(network);
            primaryKey.setDevice(device);
            return sqlSession.find(NetworkMember.class, primaryKey);
        }
    }

    /**
     * Fetches the {@link NetworkMember} of the give {@link Device}.
     *
     * @param device {@link Device} of the {@link NetworkMember}
     * @return A {@link List} containing the fetched {@link NetworkMember}'s
     */
    public static List<NetworkMember> getMembershipsOfDevice(final Device device) {
        final Session sqlSession = SQL_CONNECTION.openSession();
        final List<NetworkMember> networkMembers = sqlSession
                .createQuery("select object (n) from NetworkMember as n where n.key.device = :device", NetworkMember.class)
                .setParameter("device", device)
                .getResultList();
        sqlSession.close();
        return networkMembers;
    }

    public static List<NetworkMember> getMembershipsOfNetwork(final Network network) {
        final Session sqlSession = SQL_CONNECTION.openSession();
        final List<NetworkMember> networkMembers = sqlSession
                .createQuery("select object (n) from NetworkMember as n where n.key.network = :network", NetworkMember.class)
                .setParameter("network", network)
                .getResultList();
        sqlSession.close();
        return networkMembers;
    }

    /**
     * Returns the {@link Network} of the {@link NetworkMember}.
     *
     * @return {@link Network} of the {@link NetworkMember}
     */
    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    /**
     * Sets a new {@link Network} of the {@link NetworkMember}.
     *
     * @param network New {@link Network} to be set
     */
    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new MemberKey();
        this.key.network = network;
    }

    /**
     * Returns the {@link Device} of the {@link NetworkMember}.
     *
     * @return {@link Device} of the {@link NetworkMember}
     */
    public Device getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    /**
     * Sets a new {@link Device} of the {@link NetworkMember}.
     *
     * @param device New {@link Device} to be set
     */
    public void setDevice(final Device device) {
        if (this.key == null) this.key = new MemberKey();
        this.key.device = device;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link NetworkMember} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .build();
    }

    /**
     * Key of the {@link NetworkMember} entity.
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    @Data
    public static class MemberKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "network_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Network network;

        @ManyToOne
        @JoinColumn(name = "device_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Device device;
    }
}
