package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a device entry in the database
 *
 * @since 0.3.0
 */
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

    /**
     * Empty constructor to create a new {@link Device}
     */
    public Device() {
    }

    /**
     * Creates a new {@link Device}
     *
     * @param name      Name of the {@link Device}
     * @param owner     Owner of the {@link Device}
     * @param poweredOn Power state of the {@link Device}
     * @return The instance of the created {@link Device}
     */
    public static Device createDevice(final String name, final User owner, final boolean poweredOn) {
        final Session sqlSession = sqlConnection.openSession();

        final Device device = new Device();
        device.setName(name);
        device.setOwner(owner);
        device.setPoweredOn(poweredOn);

        sqlSession.beginTransaction();
        sqlSession.save(device);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return device;
    }

    /**
     * Fetches the {@link Device} with the given id
     *
     * @param id The id of the {@link Device}
     * @return The instance of the fetched {@link Device} if it exists | null if the entity does not exist
     */
    public static Device getById(UUID id) {
        return getById(Device.class, id);
    }

    /**
     * Returns the name of the {@link Device}
     *
     * @return Name of the {@link Device}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a new name of the {@link Device}
     *
     * @param name New name to be set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the owner of the {@link Device}
     *
     * @return Owner of the {@link Device}
     */
    public User getOwner() {
        return this.owner;
    }

    /**
     * Sets a new owner of the {@link Device}
     *
     * @param owner New owner to be set
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * Returns the power state of the {@link Device}
     *
     * @return Power state of the {@link Device}
     */
    public boolean isPoweredOn() {
        return this.poweredOn;
    }

    /**
     * Sets a new power state of the {@link Device}
     *
     * @param poweredOn New power state to be set
     */
    public void setPoweredOn(final boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    public boolean hasUserAccess(User user) {
        return getOwner().equals(user) || DeviceAccess.hasUserAccessToDevice(user, this);
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Device} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("owner", this.getOwner().getId())
                .add("powered_on", this.isPoweredOn())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link Device}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Device} | False if it does not
     */
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

    /**
     * Hashes the {@link Device} using {@link Objects} hash method
     *
     * @return Hash of the {@link Device}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getOwner(), isPoweredOn());
    }
}
