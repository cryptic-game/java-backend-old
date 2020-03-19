package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
    private LocalDateTime expire;

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

    public LocalDateTime getExpire() {
        return this.expire;
    }

    public void setExpire(final LocalDateTime expire) {
        this.expire = expire;
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
                .add("id", this.getId())
                .add("target_device", getDevice().getId())
                .add("device", getUser().getId())
                .add("granted", getAccessGranted().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("expire", getExpire().toInstant(ZoneOffset.UTC).toEpochMilli())
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
                Objects.equals(getExpire(), that.getExpire()) &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getUser(), getAccessGranted(), getExpire(), isValid());
    }


    public static boolean hasUserAccessToDevice(final User user, final Device device) { // übertragen
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.user = :user and a.device = :device and a.valid = true and a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("user", user)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList().size() > 0;
        }
    }

    public static DeviceAccess grantAccessToDevice(final User user, final Device device, final Duration duration) {
        Session sqlSession = sqlConnection.openSession();

        DeviceAccess access = new DeviceAccess();
        access.setUser(user);
        access.setDevice(device);
        access.setAccessGranted(LocalDateTime.now());
        access.setValid(true);
        access.setExpire(LocalDateTime.now().plus(duration));

        sqlSession.beginTransaction();
        sqlSession.save(access);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return access;
    }

    public static List<DeviceAccess> getAccessesToDevice(final Device device) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.device = :device and a.valid = true and a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList();
        }
    }
}
