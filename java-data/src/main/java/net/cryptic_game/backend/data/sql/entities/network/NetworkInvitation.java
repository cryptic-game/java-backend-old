package net.cryptic_game.backend.data.sql.entities.network;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing a network invitation entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "network_invitation")
public final class NetworkInvitation extends TableModel implements JsonSerializable {

    @EmbeddedId
    private InvitationKey key;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = true, updatable = false)
    @Type(type = "uuid-char")
    private Device inviter;

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

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NetworkInvitation)) return false;
        NetworkInvitation element = (NetworkInvitation) obj;
        return element.getKey().equals(this.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }


    /**
     * Key of the {@link NetworkInvitation} entity.
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    @Getter
    @Setter
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

        /**
         * Checks if the key has an equal network and device.
         *
         * @param obj the object to check
         * @return returns true if the objects have the same identifiers
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof InvitationKey)) return false;
            InvitationKey element = (InvitationKey) obj;
            return this.network.equals(element.network) && this.device.equals(element.device);
        }

        /**
         * @return the hashcode
         */
        @Override
        public int hashCode() {
            return Objects.hash(device, network);
        }
    }
}
