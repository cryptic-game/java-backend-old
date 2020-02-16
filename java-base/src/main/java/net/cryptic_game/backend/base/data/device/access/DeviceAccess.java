package net.cryptic_game.backend.base.data.device.access;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.device.Device;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "device_access")
public class DeviceAccess extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "granted", nullable = false, updatable = false)
    private LocalDateTime accessGranted;

    @Column(name = "expire", nullable = false, updatable = false)
    private LocalDateTime exprie;

    @Column(name = "valid", nullable = false, updatable = true)
    private boolean valid;

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public LocalDateTime getAccessGranted() {
        return this.accessGranted;
    }

    public void setAccessGranted(final LocalDateTime accessGranted) {
        this.accessGranted = accessGranted;
    }

    public LocalDateTime getExprie() {
        return this.exprie;
    }

    public void setExprie(final LocalDateTime exprie) {
        this.exprie = exprie;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("target_device", getDevice().getId())
                .add("device", getUser().getId())
                .add("granted", getAccessGranted().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("expire", getExprie().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("valid", isValid())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceAccess that = (DeviceAccess) o;
        return isValid() == that.isValid() &&
                Objects.equals(getDevice(), that.getDevice()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getAccessGranted(), that.getAccessGranted()) &&
                Objects.equals(getExprie(), that.getExprie()) &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getUser(), getAccessGranted(), getExprie(), isValid());
    }
}
