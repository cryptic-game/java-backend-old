package net.cryptic_game.backend.base.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device_device")
public class Device extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    @Column(name = "powered_on", updatable = true, nullable = false, columnDefinition = "TINYINT")
    private boolean poweredOn;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    public boolean isPoweredOn() {
        return this.poweredOn;
    }

    public void setPoweredOn(final boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return isPoweredOn() == device.isPoweredOn() &&
                Objects.equals(getName(), device.getName()) &&
                Objects.equals(getOwner(), device.getOwner()) &&
                getId().equals(device.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getOwner(), isPoweredOn());
    }
}
