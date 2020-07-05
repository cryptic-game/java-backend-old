package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Entity representing a device access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_access")
@Data
public final class DeviceAccess extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "granted", nullable = false, updatable = false)
    private OffsetDateTime granted;

    @Column(name = "expire", nullable = false, updatable = false)
    private OffsetDateTime expire;

    @Column(name = "valid", nullable = false, updatable = true)
    private boolean valid;

    /**
     * Checks if a {@link User} has got access to the {@link Device}.
     *
     * @param user   the {@link User}
     * @param device the {@link Device}
     * @return true if the {@link User} has got access | otherwise false
     */
    public static boolean hasAccess(final User user, final Device device) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession.createQuery("select object (a) from DeviceAccess a where "
                    + "a.user = :user and "
                    + "a.device = :device and "
                    + "a.valid = true and "
                    + "a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("user", user)
                    .setParameter("device", device)
                    .setParameter("currentDate", OffsetDateTime.now())
                    .setMaxResults(1)
                    .getResultList().size() > 0;
        }
    }

    /**
     * Grants access for a {@link User} to a {@link Device}.
     *
     * @param user     the {@link User}
     * @param device   the {@link Device}
     * @param duration the {@link Duration} how long the {@link User} has got access
     * @return the resulting {@link DeviceAccess}
     */
    public static DeviceAccess grantAccessToDevice(final User user, final Device device, final Duration duration) {
        final DeviceAccess access = new DeviceAccess();
        access.setUser(user);
        access.setDevice(device);
        access.setGranted(OffsetDateTime.now());
        access.setValid(true);
        access.setExpire(OffsetDateTime.now().plus(duration));

        access.saveOrUpdate();
        return access;
    }

    /**
     * Returns a {@link List} of {@link DeviceAccess}es, so accesses for a {@link Device}.
     *
     * @param device the {@link Device}
     * @return the {@link List} of {@link DeviceAccess}
     */
    public static List<DeviceAccess> getAccessesToDevice(final Device device) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession.createQuery("select object (a) from DeviceAccess a where a.device = :device "
                    + "and a.valid = true and a.expire > :currentDate", DeviceAccess.class)
                    .setParameter("device", device)
                    .setParameter("currentDate", OffsetDateTime.now())
                    .getResultList();
        }
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceAccess} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("user_id", this.getUser().getId())
                .add("granted", this.getGranted())
                .add("expire", this.getExpire())
                .add("valid", this.isValid())
                .build();
    }
}
