package net.cryptic_game.backend.base.data.network.invitation;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.device.Device;
import net.cryptic_game.backend.base.data.network.Network;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "network_invitation")
public class Invitation extends TableModel {

    @EmbeddedId
    private InvitationKey key;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = true, updatable = false)
    @Type(type = "uuid-char")
    private Device inviter;

    @Column(name = "request", updatable = false, nullable = false)
    private boolean request;

    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.network = network;
    }

    public Device getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    public void setDevice(final Device device) {
        if (this.key == null) this.key = new InvitationKey();
        this.key.device = device;
    }

    public boolean isRequest() {
        return this.request;
    }

    public void setRequest(final boolean request) {
        this.request = request;
    }

    public Device getInviter() {
        return this.inviter;
    }

    public void setInviter(final Device inviter) {
        this.inviter = inviter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return this.isRequest() == that.isRequest() &&
                Objects.equals(this.getNetwork(), that.getNetwork()) &&
                Objects.equals(this.getDevice(), that.getDevice()) &&
                Objects.equals(this.getInviter(), that.getInviter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNetwork(), this.getDevice(), this.isRequest(), this.getInviter());
    }

    @Override
    public JsonObject serialize() {
        JsonBuilder builder = JsonBuilder.anJSON()
                .add("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .add("request", this.isRequest());
        if (this.getInviter() != null) builder.add("inviter", this.getInviter().getId());
        return builder.build();
    }


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

        public InvitationKey() {
        }

        public InvitationKey(final Network network, final Device device) {
            this.network = network;
            this.device = device;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Invitation.InvitationKey invitationKey = (Invitation.InvitationKey) o;
            return Objects.equals(this.network, invitationKey.network) &&
                    Objects.equals(this.device, invitationKey.device);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.network, this.device);
        }
    }
}
