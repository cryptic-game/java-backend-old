package net.cryptic_game.backend.data.device;

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
public class DeviceFile extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "filename", nullable = false, updatable = true)
    private String name;

    @Column(name = "content", nullable = false, updatable = true)
    private String content;

    @Column(name = "directory", nullable = false, updatable = false, columnDefinition = "TINYINT")
    private boolean directory;

    @ManyToOne
    @JoinColumn(name = "parent_dir_id", nullable = true, updatable = true)
    @Type(type = "uuid-char")
    private DeviceFile parentDirectory;

    /**
     * Creates a {@link DeviceFile} and returns itself.
     *
     * @param device      the {@link DeviceFile}  where the file will be added
     * @param name        the name of the {@link DeviceFile}
     * @param contents    the content of the {@link DeviceFile}
     * @param isDirectory defines whether the {@link DeviceFile} is a director
     * @param parentDir   the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    private static DeviceFile createFile(final Device device, final String name, final String contents, final boolean isDirectory, final DeviceFile parentDir) {
        final Session sqlSession = SQL_CONNECTION.openSession();

        final DeviceFile file = new DeviceFile();
        file.setDevice(device);
        file.setName(name);
        file.setContent(contents);
        file.setDirectory(isDirectory);
        file.setParentDirectory(parentDir);

        sqlSession.beginTransaction();
        sqlSession.save(file);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return file;
    }

    /**
     * Creates a {@link DeviceFile} and returns itself as file, not directory.
     *
     * @param device    the {@link DeviceFile}  where the file will be added
     * @param name      the name of the {@link DeviceFile}
     * @param contents  the content of the {@link DeviceFile}
     * @param parentDir the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    public static DeviceFile createFile(final Device device, final String name, final String contents, final DeviceFile parentDir) {
        return createFile(device, name, contents, false, parentDir);
    }

    /**
     * Creates a {@link DeviceFile} and returns itself as directory.
     *
     * @param device    the {@link DeviceFile}  where the file will be added
     * @param name      the name of the {@link DeviceFile}
     * @param parentDir the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    public static DeviceFile createDirectory(final Device device, final String name, final DeviceFile parentDir) {
        return createFile(device, name, "", true, parentDir);
    }

    /**
     * Returns a list of all {@link DeviceFile} of a {@link Device}.
     *
     * @param device the {@link Device} where you want all {@link DeviceFile}
     * @return the {@link List} of all {@link DeviceFile} of a {@link Device}
     */
    public static List<DeviceFile> getFilesByDevice(final Device device) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession
                    .createQuery("select object (f) from DeviceFile f where f.device = :device", DeviceFile.class)
                    .setParameter("device", device)
                    .getResultList();
        }
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceFile} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device", this.getDevice().getId())
                .add("name", this.getName())
                .add("contents", this.getContent())
                .add("is_directory", this.isDirectory())
                .add("parent_dir", this.getParentDirectory() == null ? "null" : this.getParentDirectory().getId().toString())
                .build();
    }
}
