package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing a service entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "service_service")
public class Service extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "name", updatable = true, nullable = true)
    private String name;

    @Column(name = "running", updatable = true, nullable = true, columnDefinition = "TINYINT")
    private boolean running;

    @Column(name = "running_port", updatable = true, nullable = true)
    private int runningPort;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    /**
     * Returns the {@link Device} of the {@link Service}
     *
     * @return the {@link Device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets the {@link Device} of the {@link Service}
     *
     * @param device the new {@link Device}
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the name of the {@link Service}
     *
     * @return the name of the {@link Service}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the {@link Service}
     *
     * @param name the new Name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns whether the {@link Service} is running or not
     *
     * @return true if it does, otherwise false
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Sets whether the {@link Service} is running or not
     *
     * @param running true if it should run otherwise false
     */
    public void setRunning(final boolean running) {
        this.running = running;
    }

    /**
     * Returns the port where the {@link Service} is running
     *
     * @return the port
     */
    public int getRunningPort() {
        return this.runningPort;
    }

    /**
     * Sets the port where the {@link Service} is running
     *
     * @param runningPort the new port
     */
    public void setRunningPort(final int runningPort) {
        this.runningPort = runningPort;
    }

    /**
     * Returns the {@link User}
     *
     * @return the {@link User}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the {@link User}
     *
     * @param user the new {@link User}
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Service} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("name", this.getName())
                .add("isRunning", this.isRunning())
                .add("runningPort", this.getRunningPort())
                .add("user", this.getUser().getId())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link Service}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Service} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return isRunning() == service.isRunning() &&
                getRunningPort() == service.getRunningPort() &&
                Objects.equals(getDevice(), service.getDevice()) &&
                Objects.equals(getName(), service.getName()) &&
                Objects.equals(getUser(), service.getUser());
    }

    /**
     * Hashes the {@link Service} using {@link Objects} hash method
     *
     * @return Hash of the {@link Service}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getName(), isRunning(), getRunningPort(), getUser());
    }
}
