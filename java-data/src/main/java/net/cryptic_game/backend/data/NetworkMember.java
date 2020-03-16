package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing an network member entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_member")
public class NetworkMember extends TableModel {

    @EmbeddedId
    private MemberKey key;

    /**
     * Empty constructor to create a new {@link NetworkMember}
     */
    public NetworkMember() {
    }

    /**
     * Creates a new {@link NetworkMember}
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

        final Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(networkMember);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return networkMember;
    }

    /**
     * Fetches the {@link NetworkMember} with the given key
     *
     * @param network {@link Network} of the {@link NetworkMember}
     * @param device  {@link Device} of the {@link NetworkMember}
     * @return The instance of the fetched {@link NetworkMember} if it exists | null if the entity does not exist
     */
    public static NetworkMember getMember(final Network network, final Device device) {
        final Session sqlSession = sqlConnection.openSession();
        NetworkMember networkMember = sqlSession.find(NetworkMember.class, new NetworkMember.MemberKey(network, device));
        sqlSession.close();
        return networkMember;
    }

    /**
     * Returns the {@link MemberKey} of the {@link NetworkMember}
     *
     * @return {@link MemberKey} of the {@link NetworkMember}
     */
    public MemberKey getKey() {
        return this.key;
    }

    /**
     * Sets a new {@link MemberKey} of the {@link NetworkMember}
     *
     * @param key New {@link MemberKey} to be set
     */
    public void setKey(final MemberKey key) {
        this.key = key;
    }

    /**
     * Returns the {@link Network} of the {@link NetworkMember}
     *
     * @return {@link Network} of the {@link NetworkMember}
     */
    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    /**
     * Sets a new {@link Network} of the {@link NetworkMember}
     *
     * @param network New {@link Network} to be set
     */
    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new MemberKey();
        this.key.network = network;
    }

    /**
     * Returns the {@link Device} of the {@link NetworkMember}
     *
     * @return {@link Device} of the {@link NetworkMember}
     */
    public Device getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    /**
     * Sets a new {@link Device} of the {@link NetworkMember}
     *
     * @param device New {@link Device} to be set
     */
    public void setDevice(final Device device) {
        if (this.key == null) this.key = new MemberKey();
        this.key.device = device;
    }

    /**
     * Generates a {@link JsonObject} containg all relevent {@link NetworkMember} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link NetworkMember}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link NetworkMember} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        NetworkMember networkMember = (NetworkMember) o;
        return Objects.equals(this.getNetwork(), networkMember.getNetwork()) &&
                Objects.equals(this.getDevice(), networkMember.getDevice());
    }

    /**
     * Hashes the {@link NetworkMember} using {@link Objects} hash method
     *
     * @return Hash of the {@link NetworkMember}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getNetwork(), this.getDevice());
    }

    /**
     * Key of the {@link NetworkMember} entity
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    public static class MemberKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "network_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Network network;

        @ManyToOne
        @JoinColumn(name = "device_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Device device;

        /**
         * Empty constructor to create a new {@link MemberKey}
         */
        public MemberKey() {
        }

        /**
         * Creates a new {@link MemberKey}
         *
         * @param network {@link Network} of the {@link MemberKey}
         * @param device  {@link Device} of the {@link MemberKey}
         */
        public MemberKey(final Network network, final Device device) {
            this.network = network;
            this.device = device;
        }

        /**
         * Compares an {@link Object} if it equals the {@link MemberKey}
         *
         * @param o {@link Object} to compare
         * @return True if the {@link Object} equals the {@link MemberKey} | False if it does not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            MemberKey memberKey = (MemberKey) o;
            return Objects.equals(this.network, memberKey.network) &&
                    Objects.equals(this.device, memberKey.device);
        }

        /**
         * Hashes the {@link MemberKey} using {@link Objects} hash method
         *
         * @return Hash of the {@link MemberKey}
         */
        @Override
        public int hashCode() {
            return Objects.hash(this.network, this.device);
        }
    }
}
