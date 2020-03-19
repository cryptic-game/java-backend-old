package net.cryptic_game.backend.data;

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

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public void setIsDirectory(final boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public DeviceFile getParentDirectory() {
        return this.parentDirectory;
    }

    public void setParentDirectory(final DeviceFile parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getName(), getContent(), isDirectory(), getParentDirectory());
    }



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

    public static DeviceFile createFile(final Device device, final String name, final String contents, final DeviceFile parentDir) {
        return createFile(device, name, contents, false, parentDir);
    }

    public static DeviceFile createDirectory(final Device device, final String name, final DeviceFile parentDir) {
        return createFile(device, name, "", true, parentDir);
    }

    public static List<DeviceFile> getFilesByDevice(final Device device) {
        try (final Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (f) from File f where f.device = :device", DeviceFile.class)
                    .setParameter("device", device)
                    .getResultList();
        }
    }
}
