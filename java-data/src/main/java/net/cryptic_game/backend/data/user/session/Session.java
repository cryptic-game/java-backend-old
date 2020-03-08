package net.cryptic_game.backend.data.user.session;

import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.cryptic_game.backend.base.sql.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "session")
public class Session extends TableModelAutoId {

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

    public User getUser() {
        return this.user;
    }

    protected void setUser(final User user) {
        this.user = user;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    protected void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    public LocalDateTime getExpire() {
        return this.expire;
    }

    protected void setExpire(final LocalDateTime expire) {
        this.expire = expire;
    }

    public boolean isValid() {
        return this.valid;
    }

    protected void setValid(final boolean valid) {
        this.valid = valid;
    }

    public LocalDateTime getLastActive() {
        return this.lastActive;
    }

    protected void setLastActive(final LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public String getTokenHash() {
        return this.tokenHash;
    }

    public void setTokenHash(final String tokenHash) {
        this.tokenHash = tokenHash;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getVersion(), this.getDeviceName(), this.getExpire(),
                this.getLastActive(), this.isValid(), this.getTokenHash());
    }
}
