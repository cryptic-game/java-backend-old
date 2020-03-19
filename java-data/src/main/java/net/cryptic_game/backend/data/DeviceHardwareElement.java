package net.cryptic_game.backend.data;

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

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceHardwareElement that = (DeviceHardwareElement) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getManufacturer(), that.getManufacturer()) &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getManufacturer());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("manufacturer", this.getManufacturer())
                .build();
    }
}
