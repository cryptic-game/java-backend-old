package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

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
     * Creates a {@link DeviceFile} and returns itself.
     *
     * @param session     the sql {@link Session} with transaction
     * @param device      the {@link DeviceFile}  where the file will be added
     * @param name        the name of the {@link DeviceFile}
     * @param contents    the content of the {@link DeviceFile}
     * @param isDirectory defines whether the {@link DeviceFile} is a director
     * @param parentDir   the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    private static DeviceFile createFile(final Session session, final Device device, final String name, final String contents,
                                         final boolean isDirectory, final DeviceFile parentDir) {
        final DeviceFile file = new DeviceFile();
        file.setDevice(device);
        file.setName(name);
        file.setContent(contents);
        file.setDirectory(isDirectory);
        file.setParentDirectory(parentDir);

        //file.saveOrUpdate(session);
        return file;
    }

    /**
     * Creates a {@link DeviceFile} and returns itself as file, not directory.
     *
     * @param session   the sql {@link Session} with transaction
     * @param device    the {@link DeviceFile}  where the file will be added
     * @param name      the name of the {@link DeviceFile}
     * @param contents  the content of the {@link DeviceFile}
     * @param parentDir the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    public static DeviceFile createFile(final Session session, final Device device, final String name, final String contents, final DeviceFile parentDir) {
        return createFile(session, device, name, contents, false, parentDir);
    }

    /**
     * Creates a {@link DeviceFile} and returns itself as directory.
     *
     * @param session   the sql {@link Session} with transaction
     * @param device    the {@link DeviceFile}  where the file will be added
     * @param name      the name of the {@link DeviceFile}
     * @param parentDir the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    public static DeviceFile createDirectory(final Session session, final Device device, final String name, final DeviceFile parentDir) {
        return createFile(session, device, name, "", true, parentDir);
    }

    /**
     * Returns a list of all {@link DeviceFile} of a {@link Device}.
     *
     * @param session the sql {@link Session}
     * @param device  the {@link Device} where you want all {@link DeviceFile}
     * @return the {@link List} of all {@link DeviceFile} of a {@link Device}
     */
    public static List<DeviceFile> getFilesByDevice(final Session session, final Device device) {
        return session.createQuery("select object (f) from DeviceFile f where f.device = :device", DeviceFile.class)
                .setParameter("device", device)
                .getResultList();
    }

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
