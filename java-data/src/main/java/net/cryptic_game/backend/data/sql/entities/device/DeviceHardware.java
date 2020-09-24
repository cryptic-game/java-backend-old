package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a device hardware entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_hardware")
public final class DeviceHardware extends TableModelAutoId implements JsonSerializable {

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
    private DeviceHardwareType type;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceHardware} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("element_id", this.getElement().getId())
                .add("type", this.getType().toString())
                .build();
    }
}
