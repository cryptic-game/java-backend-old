package net.cryptic_game.backend.base.data.session;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "device_name", nullable = false, updatable = false)
    private String deviceName;

    @Column(name = "expire", nullable = false, updatable = false)
    private LocalDateTime expire;

    @Column(name = "valid", nullable = false, updatable = false)
    private boolean valid;

    @Override
    public JsonObject serialize() {
        return null;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
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
}
