package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing a device hardware entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_hardware")
public class DeviceHardware extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "element_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private DeviceHardwareElement element;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", updatable = false, nullable = false)
    private HardwareType type;

    /**
     * Returns the {@link Device} where the {@link DeviceHardware} is located
     *
     * @return the {@link Device} where the {@link DeviceHardware} is located
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets the {@link Device} where the {@link DeviceHardware} is located
     *
     * @param device the {@link Device} where the {@link DeviceHardware} is located
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the {@link DeviceHardwareElement} of the {@link DeviceHardware}
     *
     * @return the {@link DeviceHardwareElement} of the {@link DeviceHardware}
     */
    public DeviceHardwareElement getElement() {
        return this.element;
    }

    /**
     * Sets the {@link DeviceHardwareElement} of the {@link DeviceHardware}
     *
     * @param element the new {@link DeviceHardwareElement} of the {@link DeviceHardware}
     */
    public void setElement(final DeviceHardwareElement element) {
        this.element = element;
    }

    /**
     * Returns the {@link HardwareType} (the Type of the {@link DeviceHardware})
     *
     * @return the {@link HardwareType}
     */
    public HardwareType getType() {
        return this.type;
    }

    /**
     * Sets the {@link HardwareType} (the Type of the {@link DeviceHardware})
     *
     * @param type the new {@link HardwareType}
     */
    public void setType(final HardwareType type) {
        this.type = type;
    }

    /**
     * Compares an {@link Object} if it equals the {@link DeviceHardware}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceHardware} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceHardware hardware = (DeviceHardware) o;
        return Objects.equals(getDevice(), hardware.getDevice()) &&
                Objects.equals(getElement(), hardware.getElement()) &&
                getType() == hardware.getType() &&
                Objects.equals(getId(), hardware.getId());
    }

    /**
     * Hashes the {@link DeviceHardware} using {@link Objects} hash method
     *
     * @return Hash of the {@link DeviceHardware}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getElement(), getType());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceHardware} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device", this.getDevice().getId())
                .add("element", this.getElement().getId())
                .add("type", this.getType().toString())
                .build();
    }

    public enum HardwareType {

        PROCESSOR, MAINBOARD, RAM, COOLER, GRAPHIC_CARD, DISK, POWER_PACK, CASE
    }

}
