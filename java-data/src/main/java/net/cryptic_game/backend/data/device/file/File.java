package net.cryptic_game.backend.data.device.file;

import com.google.gson.JsonObject;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.cryptic_game.backend.base.sql.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.device.Device;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "device_file")
public class File extends TableModelAutoId {

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
  private File parentDirectory;

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

  public File getParentDirectory() {
    return this.parentDirectory;
  }

  public void setParentDirectory(final File parentDirectory) {
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
        .add("parent_dir", this.getParentDirectory() == null ? "null"
            : this.getParentDirectory().getId().toString())
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    File file = (File) o;
    return isDirectory() == file.isDirectory() &&
        Objects.equals(getDevice(), file.getDevice()) &&
        Objects.equals(getName(), file.getName()) &&
        Objects.equals(getContent(), file.getContent()) &&
        Objects.equals(getParentDirectory(), file.getParentDirectory()) &&
        Objects.equals(getId(), file.getId());
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(getId(), getDevice(), getName(), getContent(), isDirectory(), getParentDirectory());
  }
}
