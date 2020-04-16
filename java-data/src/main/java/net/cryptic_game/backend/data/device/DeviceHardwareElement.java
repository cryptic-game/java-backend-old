package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "device_hardware_element")
public class DeviceHardwareElement extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @Column(name = "manufacturer", updatable = false, nullable = false)
    private String manufacturer;

    /**
     * Returns the name of the {@link DeviceHardwareElement}
     *
     * @return the name of the {@link DeviceHardwareElement}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the the name of the {@link DeviceHardwareElement}
     *
     * @param name the new name for the {@link DeviceHardwareElement}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the Manufacturer of the {@link DeviceHardwareElement}
     *
     * @return the Manufacturer of the {@link DeviceHardwareElement}
     */
    public String getManufacturer() {
        return this.manufacturer;
    }

    /**
     * Sets the Manufacturer of the {@link DeviceHardwareElement}
     *
     * @param manufacturer the new Manufacturer of the {@link DeviceHardwareElement}
     */
    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Compares an {@link Object} if it equals the {@link DeviceHardwareElement}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceHardwareElement} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceHardwareElement that = (DeviceHardwareElement) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getManufacturer(), that.getManufacturer()) &&
                Objects.equals(getId(), that.getId());
    }

    /**
     * Hashes the {@link DeviceHardwareElement} using {@link Objects} hash method
     *
     * @return Hash of the {@link DeviceHardwareElement}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getManufacturer());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceHardwareElement} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("manufacturer", this.getManufacturer())
                .build();
    }
}
