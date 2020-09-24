package net.cryptic_game.backend.data.sql.entities.service;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a service entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_service")
public final class Service extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @Column(name = "running", updatable = true, nullable = false)
    private boolean running;

    @Column(name = "running_port", updatable = true, nullable = false)
    private int runningPort;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Service} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("name", this.getName())
                .add("is_running", this.isRunning())
                .add("running_port", this.getRunningPort())
                .add("user_id", this.getUser().getId())
                .build();
    }
}
