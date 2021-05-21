package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.getnova.framework.jpa.model.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Entity representing a device access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_access")
public final class DeviceAccess extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "granted", nullable = false, updatable = false)
    private OffsetDateTime granted;

    @Column(name = "expire", nullable = false, updatable = false)
    private OffsetDateTime expire;

    @Column(name = "valid", nullable = false, updatable = true)
    private boolean valid;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceAccess} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("user_id", this.getUser().getId())
                .add("granted", this.getGranted())
                .add("expire", this.getExpire())
                .add("valid", this.isValid())
                .build();
    }
}
