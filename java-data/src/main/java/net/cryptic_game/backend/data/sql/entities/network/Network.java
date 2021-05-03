package net.cryptic_game.backend.data.sql.entities.network;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.sql.entities.device.Device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;


/**
 * Entity representing a network entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "network_network")
public final class Network extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = true, nullable = false)
    private Device owner;

    @Column(name = "public", updatable = true, nullable = false)
    private boolean isPublic;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Network} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("owner", this.getOwner().getId())
                .add("public", this.isPublic())
                .add("created", this.getCreated())
                .build();
    }
}
