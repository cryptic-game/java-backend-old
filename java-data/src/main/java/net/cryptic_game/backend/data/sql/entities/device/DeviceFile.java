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
 * Entity representing a device file entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_file")
@Data
public final class DeviceFile extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "content", nullable = false, updatable = true, length = 2048)
    private String content;

    @Column(name = "directory", nullable = false, updatable = false)
    private boolean directory;

    @ManyToOne
    @JoinColumn(name = "parent_directory_id", nullable = true, updatable = true)
    @Type(type = "uuid-char")
    private DeviceFile parentDirectory;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceFile} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("name", this.getName())
                .add("content", this.getContent())
                .add("is_directory", this.isDirectory())
                .add("parent_directory_id", this.getParentDirectory() == null ? null : this.getParentDirectory().getId())
                .build();
    }
}
