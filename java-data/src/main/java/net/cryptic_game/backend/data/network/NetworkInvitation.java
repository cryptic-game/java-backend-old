package net.cryptic_game.backend.data.network;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
 * Entity representing a network invitation entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_invitation")
@Data
public final class NetworkInvitation extends TableModel implements JsonSerializable {

    @EmbeddedId
    private InvitationKey key;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = true, updatable = false)
    @Type(type = "uuid-char")
    private Device inviter;

    /**
     * Creates a new {@link NetworkInvitation}.
     *
     * @param session the sql session
     * @param network {@link Network} of the {@link NetworkInvitation}
     * @param device  {@link Device} of the {@link NetworkInvitation}
     * @param inviter Inviter {@link Device} of the {@link NetworkInvitation} | null if the {@link NetworkInvitation} is a request
     * @return The Instance of the created {@link NetworkInvitation}
     */
    public static NetworkInvitation createInvitation(final Session session, final Network network, final Device device, final Device inviter) {
        final NetworkInvitation networkInvitation = new NetworkInvitation();
        networkInvitation.setNetwork(network);
        networkInvitation.setDevice(device);
        networkInvitation.setInviter(inviter);

        networkInvitation.saveOrUpdate(session);
        return networkInvitation;
    }

    /**
     * Fetches the {@link NetworkInvitation} with the given key.
     *
     * @param session the sql session
     * @param network {@link Network} of the {@link NetworkInvitation}
     * @param device  {@link Device} of the {@link NetworkInvitation}
     * @return The instance of the fetched {@link NetworkInvitation} if it exists | null if the entity does not exist
     */
    public static NetworkInvitation getInvitation(final Session session, final Network network, final Device device) {
            return session.find(NetworkInvitation.class, new NetworkInvitation.InvitationKey(network, device));
    }

    /**
     * Fetches the {@link NetworkInvitation}s with the given device.
     *
     * @param session the sql session
     * @param device {@link Device} of the {@link NetworkInvitation}
     * @return A {@link List} with the fetched {@link NetworkInvitation}s
     */
    public static List<NetworkInvitation> getInvitationsOfDevice(final Session session, final Device device) {
        return session.createQuery("select object (n) from NetworkInvitation as n where n.key.device = :device", NetworkInvitation.class)
                .setParameter("device", device)
                .getResultList();
    }

    public static List<NetworkInvitation> getInvitationsOfNetwork(final Session session, final Network network) {
        return session.createQuery("select object (n) from NetworkInvitation as n where n.key.network = :network", NetworkInvitation.class)
                .setParameter("network", network)
                .getResultList();
    }

    /**
     * Returns the {@link Network} of the {@link NetworkInvitation}.
     *
     * @return {@link Network} of the {@link NetworkInvitation}
     */
    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    /**
     * Sets a new {@link Network} of the {@link NetworkInvitation}.
     *
     * @param network New {@link Network} to be set
     */
    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.network = network;
    }

    /**
     * Returns the {@link Device} of the {@link NetworkInvitation}.
     *
     * @return {@link Device} of the {@link NetworkInvitation}
     */
    public Device getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    /**
     * Sets a new {@link Device} of the {@link NetworkInvitation}.
     *
     * @param device New {@link Device} to be set
     */
    public void setDevice(final Device device) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.device = device;
    }

    /**
     * Returns whether the {@link NetworkInvitation} is a request.
     *
     * @return true if the {@link NetworkInvitation} is a request | false if it is not
     */
    public boolean isRequest() {
        return this.inviter == null;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link NetworkInvitation} information.
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
     * Key of the {@link NetworkInvitation} entity.
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvitationKey implements Serializable {

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
