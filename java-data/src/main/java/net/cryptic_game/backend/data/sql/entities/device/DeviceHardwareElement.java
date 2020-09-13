package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a device hardware element entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_hardware_element")
@Data
public final class DeviceHardwareElement extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "manufacturer", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private DeviceHardwareManufacturer manufacturer;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceHardwareElement} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("manufacturer_id", this.getManufacturer().getId())
                .build();
    }
}
