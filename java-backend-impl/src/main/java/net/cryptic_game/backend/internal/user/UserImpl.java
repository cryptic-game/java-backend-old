package net.cryptic_game.backend.internal.user;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.User;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Entity representing an user entry in the database
 *
 * @see UserWrapper Management class
 * @since 0.3.0
 */
@Entity
@Table(name = "user")
public class UserImpl extends TableModelAutoId implements User {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @Column(name = "mail", updatable = true, nullable = false, unique = true)
    private String mail;

    @Column(name = "password", updatable = true, nullable = false)
    private String passwordHash;

    @Column(name = "created", updatable = false, nullable = false)
    private LocalDateTime created;

    @Column(name = "last", updatable = true, nullable = false)
    private LocalDateTime last;

    /**
     * Empty constructor to create a new {@link UserImpl}
     */
    public UserImpl() {
    }

    /**
     * Generates a {@link JsonObject} containg all relevent {@link UserImpl} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        final JsonObject json = new JsonObject();
        json.addProperty("id", this.getId().toString());
        json.addProperty("name", this.getName());
        json.addProperty("mail", this.getMail());
        json.addProperty("created", this.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli());
        json.addProperty("last", this.getLast().toInstant(ZoneOffset.UTC).toEpochMilli());
        return json;
    }

    /**
     * Returns the name of the {@link UserImpl}
     *
     * @return Name of the {@link UserImpl}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name of the {@link UserImpl}
     *
     * @param name New name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the mail of the {@link UserImpl}
     *
     * @return Mail of the {@link UserImpl}
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets a new mail of the {@link UserImpl}
     *
     * @param mail New mail to be set
     */
    public void setMail(final String mail) {
        this.mail = mail;
    }

    /**
     * Returns the password hash of the {@link UserImpl}
     *
     * @return Password hash of the {@link UserImpl}
     */
    public String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Sets a new password hash of the {@link UserImpl}
     *
     * @param passwordHash New password hash to be set
     */
    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the creation date of the {@link UserImpl}
     *
     * @return Creation date of the {@link UserImpl}
     */
    public LocalDateTime getCreated() {
        return this.created;
    }

    /**
     * Sets a new creation date of the {@link UserImpl}
     *
     * @param created New creation date to be set
     */
    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    /**
     * Returns the last-seen date of the {@link UserImpl}
     *
     * @return Last-seen date of the {@link UserImpl}
     */
    public LocalDateTime getLast() {
        return this.last;
    }

    /**
     * Sets a new last-seen date of the {@link UserImpl}
     *
     * @param last New last-seen date to be set
     */
    public void setLast(final LocalDateTime last) {
        this.last = last;
    }

    /**
     * Compares an {@link Object} if it equals the {@link UserImpl}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link UserImpl} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImpl that = (UserImpl) o;
        return Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(this.getVersion(), that.getVersion()) &&
                Objects.equals(this.getCreated(), that.getCreated()) &&
                Objects.equals(this.getLast(), that.getLast()) &&
                Objects.equals(this.getMail(), that.getMail()) &&
                Objects.equals(this.getName(), that.getName()) &&
                Objects.equals(this.getPasswordHash(), that.getPasswordHash());
    }

    /**
     * Hashes the {@link UserImpl} using {@link Objects} hash method
     *
     * @return Hash of the {@link UserImpl}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getVersion(), this.getCreated(), this.getLast(),
                this.getMail(), this.getName(), this.getPasswordHash());
    }
}
