package net.cryptic_game.backend.internal.network.member;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.internal.device.DeviceImpl;
import net.cryptic_game.backend.internal.network.Network;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "network_member")
public class Member extends TableModel {

    @EmbeddedId
    private MemberKey key;

    public MemberKey getKey() {
        return this.key;
    }

    public void setKey(final MemberKey key) {
        this.key = key;
    }

    public Network getNetwork() {
        if (this.key == null) return null;
        return this.key.network;
    }

    public void setNetwork(final Network network) {
        if (this.key == null) this.key = new MemberKey();
        this.key.network = network;
    }

    public DeviceImpl getDevice() {
        if (this.key == null) return null;
        return this.key.device;
    }

    public void setDevice(final DeviceImpl device) {
        if (this.key == null) this.key = new MemberKey();
        this.key.device = device;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("network", this.getNetwork().getId())
                .add("device", this.getDevice().getId())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(this.getNetwork(), member.getNetwork()) &&
                Objects.equals(this.getDevice(), member.getDevice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNetwork(), this.getDevice());
    }

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
        private DeviceImpl device;

        public MemberKey() {
        }

        public MemberKey(final Network network, final DeviceImpl device) {
            this.network = network;
            this.device = device;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            MemberKey memberKey = (MemberKey) o;
            return Objects.equals(this.network, memberKey.network) &&
                    Objects.equals(this.device, memberKey.device);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.network, this.device);
        }
    }
}
