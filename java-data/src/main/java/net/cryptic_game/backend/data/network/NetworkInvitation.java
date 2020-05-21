package net.cryptic_game.backend.data.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.data.device.Device;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a network invitation entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_invitation")
public class NetworkInvitation extends TableModel implements JsonSerializable {

    @EmbeddedId
    private InvitationKey key;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = true, updatable = false)
    @Type(type = "uuid-char")
    private Device inviter;

    /**
     * Empty constructor to create a new {@link NetworkInvitation}
     */
    public NetworkInvitation() {
    }

    /**
     * Creates a new {@link NetworkInvitation}
     *
     * @param network {@link Network} of the {@link NetworkInvitation}
     * @param device  {@link Device} of the {@link NetworkInvitation}
     * @param inviter Inviter {@link Device} of the {@link NetworkInvitation} | null if the {@link NetworkInvitation} is a request
     * @return The Instance of the created {@link NetworkInvitation}
     */
    public static NetworkInvitation createInvitation(final Network network, final Device device, final Device inviter) {
        final NetworkInvitation networkInvitation = new NetworkInvitation();
        networkInvitation.setNetwork(network);
        networkInvitation.setDevice(device);
        networkInvitation.setInviter(inviter);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(networkInvitation);
        session.getTransaction().commit();
        session.close();
        return networkInvitation;
    }

    /**
     * Fetches the {@link NetworkInvitation} with the given key
     *
     * @param network {@link Network} of the {@link NetworkInvitation}
     * @param device  {@link Device} of the {@link NetworkInvitation}
     * @return The instance of the fetched {@link NetworkInvitation} if it exists | null if the entity does not exist
     */
    public static NetworkInvitation getInvitation(final Network network, final Device device) {
        final Session sqlSession = sqlConnection.openSession();
        NetworkInvitation networkInvitation = sqlSession.find(NetworkInvitation.class, new NetworkInvitation.InvitationKey(network, device));
        sqlSession.close();
        return networkInvitation;
    }

    public static List<NetworkInvitation> getInvitationsOfNetwork(final Network network) {
        final Session sqlSession = sqlConnection.openSession();
        final List<NetworkInvitation> networkInvitations = sqlSession
                .createQuery("select object (n) from NetworkInvitation as n where n.key.network = :network", NetworkInvitation.class)
                .setParameter("network", network)
                .getResultList();
        sqlSession.close();
        return networkInvitations;
    }

    /**
     * Returns the {@link InvitationKey} of the {@link NetworkInvitation}
     *
     * @return {@link InvitationKey} of the {@link NetworkInvitation}
     */
    public InvitationKey getKey() {
        return this.key;
    }

    /**
     * Sets a new {@link InvitationKey} of the {@link NetworkInvitation}
     *
     * @param key New {@link InvitationKey} to be set
     */
    public void setKey(final InvitationKey key) {
        this.key = key;
    }

    /**
     * Returns the {@link Network} of the {@link NetworkInvitation}
     *
     * @return {@link Network} of the {@link NetworkInvitation}
     */
    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    /**
     * Sets a new {@link Network} of the {@link NetworkInvitation}
     *
     * @param network New {@link Network} to be set
     */
    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.network = network;
    }

    /**
     * Returns the {@link Device} of the {@link NetworkInvitation}
     *
     * @return {@link Device} of the {@link NetworkInvitation}
     */
    public Device getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    /**
     * Sets a new {@link Device} of the {@link NetworkInvitation}
     *
     * @param device New {@link Device} to be set
     */
    public void setDevice(final Device device) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.device = device;
    }

    /**
     * Returns whether the {@link NetworkInvitation} is a request
     *
     * @return true if the {@link NetworkInvitation} is a request | false if it is not
     */
    public boolean isRequest() {
        return this.inviter == null;
    }

    /**
     * Returns the inviter {@link Device} of the {@link NetworkInvitation}
     *
     * @return inviter {@link Device} of the {@link NetworkInvitation}
     */
    public Device getInviter() {
        return this.inviter;
    }

    /**
     * Sets a new inviter {@link Device} of the {@link NetworkInvitation}
     *
     * @param inviter New inviter {@link Device} to be set
     */
    public void setInviter(final Device inviter) {
        this.inviter = inviter;
    }

    /**
     * Compares an {@link Object} if it equals the {@link NetworkInvitation}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link NetworkInvitation} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        NetworkInvitation that = (NetworkInvitation) o;
        return this.isRequest() == that.isRequest() &&
                Objects.equals(this.getNetwork(), that.getNetwork()) &&
                Objects.equals(this.getDevice(), that.getDevice()) &&
                Objects.equals(this.getInviter(), that.getInviter());
    }

    /**
     * Hashes the {@link NetworkInvitation} using {@link Objects} hash method
     *
     * @return Hash of the {@link NetworkInvitation}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getNetwork(), this.getDevice(), this.isRequest(), this.getInviter());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link NetworkInvitation} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .add("request", this.isRequest())
                .add("inviter", this.getInviter() != null, () -> this.getInviter().getId())
                .build();
    }

    /**
     * Key of the {@link NetworkInvitation} entity
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    public static class InvitationKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "network_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Network network;

        @ManyToOne
        @JoinColumn(name = "device_id", nullable = false, updatable = false)
        @Type(type = "uuid-char")
        private Device device;

        /**
         * Empty constructor to create a new {@link InvitationKey}
         */
        public InvitationKey() {
        }

        /**
         * Creates a new {@link InvitationKey}
         *
         * @param network {@link Network} of the {@link InvitationKey}
         * @param device  {@link Device} of the {@link InvitationKey}
         */
        public InvitationKey(final Network network, final Device device) {
            this.network = network;
            this.device = device;
        }

        /**
         * Compares an {@link Object} if it equals the {@link InvitationKey}
         *
         * @param o {@link Object} to compare
         * @return True if the {@link Object} equals the {@link InvitationKey} | False if it does not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            NetworkInvitation.InvitationKey invitationKey = (NetworkInvitation.InvitationKey) o;
            return Objects.equals(this.network, invitationKey.network) &&
                    Objects.equals(this.device, invitationKey.device);
        }

        /**
         * Hashes the {@link InvitationKey} using {@link Objects} hash method
         *
         * @return Hash of the {@link InvitationKey}
         */
        @Override
        public int hashCode() {
            return Objects.hash(this.network, this.device);
        }
    }
}
