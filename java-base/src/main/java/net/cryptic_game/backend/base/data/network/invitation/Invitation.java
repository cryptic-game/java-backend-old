package net.cryptic_game.backend.base.data.network.invitation;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.device.Device;
import net.cryptic_game.backend.base.data.network.Network;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "network_invitation")
public class Invitation extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "network_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Network network;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "request", updatable = false, nullable = false)
    private boolean request;

    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(final Network network) {
        this.network = network;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public boolean isRequest() {
        return this.request;
    }

    public void setRequest(final boolean request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return this.isRequest() == that.isRequest() &&
                Objects.equals(this.getNetwork(), that.getNetwork()) &&
                Objects.equals(this.getDevice(), that.getDevice()) &&
                this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getNetwork(), this.getDevice(), this.isRequest());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .add("request", this.isRequest())
                .build();
    }
}
