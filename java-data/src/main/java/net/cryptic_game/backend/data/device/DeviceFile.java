package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "device_file")
public class DeviceFile extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "filename", nullable = false, updatable = true)
    private String name;

    @Column(name = "content", nullable = false, updatable = true)
    private String content;

    @Column(name = "is_directory", nullable = false, updatable = false, columnDefinition = "TINYINT")
    private boolean isDirectory;

    @ManyToOne
    @JoinColumn(name = "parent_dir_id", nullable = true, updatable = true)
    @Type(type = "uuid-char")
    private DeviceFile parentDirectory;

    /**
     * Creates a {@link DeviceFile} and returns itself
     *
     * @param device      the {@link DeviceFile}  where the file will be added
     * @param name        the name of the {@link DeviceFile}
     * @param contents    the content of the {@link DeviceFile}
     * @param isDirectory defines whether the {@link DeviceFile} is a director
     * @param parentDir   the parent-directory as {@link DeviceFile}
     * @return the created {@link DeviceFile}
     */
    private static DeviceFile createFile(final Device device, final String name, final String contents, final boolean isDirectory, final DeviceFile parentDir) {
        final Session sqlSession = sqlConnection.openSession();

        final DeviceFile file = new DeviceFile();
        file.setDevice(device);
        file.setName(name);
        file.setContent(contents);
        file.setIsDirectory(isDirectory);
        file.setParentDirectory(parentDir);

        sqlSession.beginTransaction();
        sqlSession.save(file);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return file;
    }

    /**
     * Creates a {@link DeviceFile} and returns itself as file, not directory
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
     * Creates a {@link DeviceFile} and returns itself as directory
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
     * Returns a list of all {@link DeviceFile} of a {@link Device}
     *
     * @param device the {@link Device} where you want all {@link DeviceFile}
     * @return the {@link List} of all {@link DeviceFile} of a {@link Device}
     */
    public static List<DeviceFile> getFilesByDevice(final Device device) {
        try (final Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (f) from DeviceFile f where f.device = :device", DeviceFile.class)
                    .setParameter("device", device)
                    .getResultList();
        }
    }

    /**
     * Returns the {@link Device} where the {@link DeviceFile} is located
     *
     * @return the {@link Device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets the {@link Device} where the {@link DeviceFile} is located
     *
     * @param device New {@link Device} to be set.
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the Name of the {@link DeviceFile}
     *
     * @return the Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the {@link DeviceFile}
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the Content of the {@link DeviceFile}
     *
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Sets the content of the {@link DeviceFile}
     *
     * @param content the new content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Returns whether the {@link DeviceFile} is a directory (true) or not (false)
     *
     * @return whether the {@link DeviceFile} is a directory
     */
    public boolean isDirectory() {
        return this.isDirectory;
    }

    /**
     * Sets whether the {@link DeviceFile} is a directory (true) or not (false)
     *
     * @param isDirectory depends whether the {@link DeviceFile} is a direcory or not
     */
    public void setIsDirectory(final boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * Returns the parentdirectory of the {@link DeviceFile}
     *
     * @return the parentdirectory of the {@link DeviceFile}
     */
    public DeviceFile getParentDirectory() {
        return this.parentDirectory;
    }

    /**
     * Sets the parentdirectory of the {@link DeviceFile}
     *
     * @param parentDirectory the new parentdirectory
     */
    public void setParentDirectory(final DeviceFile parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceFile} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device", this.getDevice().getId())
                .add("name", this.getName())
                .add("contents", this.getContent())
                .add("is_directory", this.isDirectory())
                .add("parent_dir", this.getParentDirectory() == null ? "null" : this.getParentDirectory().getId().toString())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link DeviceFile}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceFile} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceFile file = (DeviceFile) o;
        return isDirectory() == file.isDirectory() &&
                Objects.equals(getDevice(), file.getDevice()) &&
                Objects.equals(getName(), file.getName()) &&
                Objects.equals(getContent(), file.getContent()) &&
                Objects.equals(getParentDirectory(), file.getParentDirectory()) &&
                Objects.equals(getId(), file.getId());
    }

    /**
     * Hashes the {@link DeviceFile} using {@link Objects} hash method
     *
     * @return Hash of the {@link DeviceFile}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getName(), getContent(), isDirectory(), getParentDirectory());
    }
}
