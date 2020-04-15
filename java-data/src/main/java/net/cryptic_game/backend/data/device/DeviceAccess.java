package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
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

    /**
     * Checks if a {@link User} has got access to the {@link Device}
     *
     * @param user   the {@link User}
     * @param device the {@link Device}
     * @return true if the {@link User} has got access | otherwise false
     */
    public static boolean hasUserAccessToDevice(final User user, final Device device) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.user = :user and a.device = :device and a.valid = true and a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("user", user)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList().size() > 0;
        }
    }

    /**
     * Grants access for a {@link User} to a {@link Device}
     *
     * @param user     the {@link User}
     * @param device   the {@link Device}
     * @param duration the {@link Duration} how long the {@link User} has got access
     * @return the resulting {@link DeviceAccess}
     */
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

    /**
     * Returns a {@link List} of {@link DeviceAccess}es, so accesses for a {@link Device}
     *
     * @param device the {@link Device}
     * @return the {@link List} of {@link DeviceAccess}
     */
    public static List<DeviceAccess> getAccessesToDevice(final Device device) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (a) from DeviceAccess a where a.device = :device and a.valid = true and a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("device", device)
                    .setParameter("currentDate", LocalDateTime.now())
                    .getResultList();
        }
    }

    /**
     * Returns the {@link Device} of the {@link DeviceAccess}
     *
     * @return the {@link Device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets a new {@link Device} for the {@link DeviceAccess}
     *
     * @param device the new {@link Device} to be set
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the {@link User} of the {@link DeviceAccess}
     *
     * @return the {@link User}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets a new {@link User} for the {@link DeviceAccess}
     *
     * @param user the new {@link DeviceAccess}
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Returns the {@link LocalDateTime} when the {@link DeviceAccess} has been granted
     *
     * @return the time accessed
     */
    public LocalDateTime getAccessGranted() {
        return this.accessGranted;
    }

    /**
     * Sets a new {@link LocalDateTime} as time accessed
     *
     * @param accessGranted the new {@link LocalDateTime} to be set
     */
    public void setAccessGranted(final LocalDateTime accessGranted) {
        this.accessGranted = accessGranted;
    }

    /**
     * Returns the {@link LocalDateTime}
     *
     * @return the time the access will expire
     */
    public LocalDateTime getExpire() {
        return this.expire;
    }

    /**
     * Sets a new {@link LocalDateTime}
     *
     * @param expire the new {@link LocalDateTime} to be set
     */
    public void setExpire(final LocalDateTime expire) {
        this.expire = expire;
    }

    /**
     * Returns whether the {@link DeviceAccess} is valid or not
     *
     * @return true if it is otherwise false
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Sets a new boolean if the {@link DeviceAccess} is valid
     *
     * @param valid the new boolean to be set
     */
    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceAccess} information
     *
     * @return The generated {@link JsonObject}
     */
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

    /**
     * Compares an {@link Object} if it equals the {@link DeviceAccess}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceAccess} | False if it does not
     */
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

    /**
     * Hashes the {@link DeviceAccess} using {@link Objects} hash method
     *
     * @return Hash of the {@link DeviceAccess}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getUser(), getAccessGranted(), getExpire(), isValid());
    }
}
