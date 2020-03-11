package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an session entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "session")
public class Session extends TableModelAutoId {

    private static final Duration EXPIRE;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        final Config config = app.getConfig();
        EXPIRE = Duration.of(config.getAsInt(BaseConfig.SESSION_EXPIRE), ChronoUnit.DAYS);
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "token", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "device_name", nullable = false, updatable = false)
    private String deviceName;

    @Column(name = "expire", nullable = false, updatable = false)
    private LocalDateTime expire;

    @Column(name = "valid", nullable = false, updatable = true)
    private boolean valid;

    @Column(name = "last_active", nullable = false, updatable = true)
    private LocalDateTime lastActive;

    /**
     * Empty constructor to create a new {@link Session}
     */
    public Session() {
    }

    /**
     * Creates a new {@link Session}
     *
     * @param user       {@link User} of the {@link Session}
     * @param token      token of the {@link Session}
     * @param deviceName device name of the {@link Session}
     * @return The instance of the created {@link Session}
     */
    public static Session createSession(final User user, final UUID token, final String deviceName) {
        final Session session = new Session();
        session.setUser(user);
        session.setTokenHash(SecurityUtils.hash(token.toString()));
        session.setDeviceName(deviceName);
        session.setExpire(LocalDateTime.now().plus(EXPIRE));
        session.setLastActive(LocalDateTime.now());
        session.setValid(true);

        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();

        return session;
    }

    /**
     * Fetches the {@link Session} with the given id
     *
     * @param id The id of the {@link Session}
     * @return The instance of the fetched {@link Session} if it exists | null if the entity does not exist
     */
    public static Session getById(final UUID id) {
        return getById(Session.class, id);
    }

    /**
     * Returns the {@link User} of the {@link Session}
     *
     * @return User of the {@link Session}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets a new {@link User} of the {@link Session}
     *
     * @param user New {@link User} to be set
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Returns the device name of the {@link Session}
     *
     * @return Device name of the {@link Session}
     */
    public String getDeviceName() {
        return this.deviceName;
    }

    /**
     * Sets a new device name of the {@link Session}
     *
     * @param deviceName New device name to be set
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Returns the expire date of the {@link Session}
     *
     * @return Expire date of the {@link Session}
     */
    public LocalDateTime getExpire() {
        return this.expire;
    }

    /**
     * Sets a new expire date of the {@link Session}
     *
     * @param expire New expire date to be set
     */
    public void setExpire(final LocalDateTime expire) {
        this.expire = expire;
    }

    /**
     * Returns whether the {@link Session} is valid
     *
     * @return True if the {@link Session} is valid | false if it is not
     */
    public boolean isValid() {
        return this.valid && LocalDateTime.now().isBefore(this.getExpire());
    }

    /**
     * Sets a valid boolean name of the {@link Session}
     *
     * @param valid New valid boolean name to be set
     */
    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    /**
     * Returns the last-active date of the {@link Session}
     *
     * @return Last-active date of the {@link Session}
     */
    public LocalDateTime getLastActive() {
        return this.lastActive;
    }

    /**
     * Sets a new last-active date of the {@link Session}
     *
     * @param lastActive New last-active date name to be set
     */
    public void setLastActive(final LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    /**
     * Returns the token hash of the {@link Session}
     *
     * @return Token hash of the {@link Session}
     */
    public String getTokenHash() {
        return this.tokenHash;
    }

    /**
     * Sets a new token hash of the {@link Session}
     *
     * @param tokenHash New token hash to be set
     */
    public void setTokenHash(final String tokenHash) {
        this.tokenHash = tokenHash;
    }

    /**
     * Generates a {@link JsonObject} containg all relevent {@link Session} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("user", this.getId())
                .add("device_name", this.getDeviceName())
                .add("valid", this.isValid())
                .add("expire", this.getExpire().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("last_active", this.getLastActive().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link Session}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Session} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session that = (Session) o;
        return valid == that.isValid() &&
                Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(this.getVersion(), that.getVersion()) &&
                Objects.equals(this.getDeviceName(), that.getDeviceName()) &&
                Objects.equals(this.getExpire(), that.getExpire()) &&
                Objects.equals(this.getLastActive(), that.getLastActive()) &&
                Objects.equals(this.getTokenHash(), that.getTokenHash());
    }

    /**
     * Hashes the {@link Session} using {@link Objects} hash method
     *
     * @return Hash of the {@link Session}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getVersion(), this.getDeviceName(), this.getExpire(),
                this.getLastActive(), this.isValid(), this.getTokenHash());
    }
}
