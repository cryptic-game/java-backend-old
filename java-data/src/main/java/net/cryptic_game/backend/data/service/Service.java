package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.user.User;
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
@Table(name = "service_service")
@Data
public class Service extends TableModelAutoId implements JsonSerializable {

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
     * Generates a {@link JsonObject} containing all relevant {@link Service} information.
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
}
