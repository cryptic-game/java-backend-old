package net.cryptic_game.backend.internal.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.device.Device;
import net.cryptic_game.backend.user.User;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "device_device")
public class DeviceImpl implements Device {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    @Column(name = "powered_on", updatable = true, nullable = false, columnDefinition = "TINYINT")
    private boolean poweredOn;

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .build();
        // TODO
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public User getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPoweredOn() {
        return this.poweredOn;
    }

    @Override
    public void setPoweredOn(boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceImpl)) return false;
        DeviceImpl device = (DeviceImpl) o;
        return isPoweredOn() == device.isPoweredOn() &&
                getName().equals(device.getName()) &&
                getOwner().equals(device.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner(), isPoweredOn());
    }
}
