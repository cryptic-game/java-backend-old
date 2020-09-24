package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a device entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_device")
public final class Device extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    @Column(name = "powered_on", updatable = true, nullable = false)
    private boolean poweredOn;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Device} information.
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
}
