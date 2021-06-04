package net.cryptic_game.backend.data.sql.entities.network;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModel;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.data.sql.entities.device.Device;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing a network member entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "network_member")
public final class NetworkMember extends TableModel implements JsonSerializable {

    @EmbeddedId
    private MemberKey key;

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

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NetworkMember)) return false;
        NetworkMember element = (NetworkMember) obj;
        return element.getKey().equals(this.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
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
        private Network network;

        @ManyToOne
        @JoinColumn(name = "device_id", nullable = false, updatable = false)
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
            if (!(obj instanceof MemberKey)) return false;
            MemberKey element = (MemberKey) obj;
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
