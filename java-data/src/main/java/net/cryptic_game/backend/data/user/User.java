package net.cryptic_game.backend.data.user;

import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an user entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "user")
public class User extends TableModelAutoId {

    @Column(name = "username", updatable = true, nullable = false, unique = true)
    private String username;

    @Column(name = "mail", updatable = true, nullable = false, unique = true)
    private String mail;

    @JsonTransient
    @Column(name = "password", updatable = true, nullable = false)
    private String passwordHash;

    @Column(name = "created", updatable = false, nullable = false)
    private ZonedDateTime created;

    @Column(name = "last", updatable = true, nullable = false)
    private ZonedDateTime last;

    /**
     * Empty constructor to create a new {@link User}
     */
    public User() {
    }

    /**
     * Create a {@link User} with the given user data
     *
     * @param username The username of the new user
     * @param mail     The mail address of the new user
     * @param password The password of the new user
     * @return The instance of the created {@link User}
     */
    public static User createUser(final String username, final String mail, final String password) {
        final ZonedDateTime now = ZonedDateTime.now();

        final User user = new User();
        user.setUsername(username);
        user.setMail(mail);
        user.setCreated(now);
        user.setLast(now);
        user.setPassword(password);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
        return user;
    }

    /**
     * Fetches the {@link User} with the given id
     *
     * @param id The id of the {@link User}
     * @return The instance of the fetched {@link User} if it exists | null if the entity does not exist
     */
    public static User getById(final UUID id) {
        return getById(User.class, id);
    }

    /**
     * Fetches the {@link User} with the give name
     *
     * @param username The username of the user
     * @return The instance of the fetched {@link User} if it exists | null if the {@link User} does not exist
     */
    public static User getByUsername(final String username) {
        final Session session = sqlConnection.openSession();
        final List<User> users = session
                .createQuery("select object (u) from User as u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        session.close();

        if (!users.isEmpty()) return users.get(0);
        return null;
    }

    /**
     * Returns the username of the {@link User}
     *
     * @return Username of the {@link User}
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username of the {@link User}
     *
     * @param username New username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the mail of the {@link User}
     *
     * @return Mail of the {@link User}
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets a new mail of the {@link User}
     *
     * @param mail New mail to be set
     */
    public void setMail(final String mail) {
        this.mail = mail;
    }

    /**
     * Returns the password hash of the {@link User}
     *
     * @return Password hash of the {@link User}
     */
    public String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Sets a new password hash of the {@link User}
     *
     * @param passwordHash New password hash to be set
     */
    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Set a new password for the given {@link User}
     *
     * @param newPassword New password to be set
     */
    public void setPassword(final String newPassword) {
        this.setPasswordHash(SecurityUtils.hash(newPassword));
    }

    /**
     * Checks whether a password matches the current password of the {@link User}
     *
     * @param input The password to be checked
     * @return true if the password is correct | false if the password is wrong
     */
    public boolean verifyPassword(final String input) {
        return SecurityUtils.verify(input, this.getPasswordHash());
    }

    /**
     * Returns the creation date of the {@link User}
     *
     * @return Creation date of the {@link User}
     */
    public ZonedDateTime getCreated() {
        return this.created;
    }

    /**
     * Sets a new creation date of the {@link User}
     *
     * @param created New creation date to be set
     */
    public void setCreated(final ZonedDateTime created) {
        this.created = created;
    }

    /**
     * Returns the last-seen date of the {@link User}
     *
     * @return Last-seen date of the {@link User}
     */
    public ZonedDateTime getLast() {
        return this.last;
    }

    /**
     * Sets a new last-seen date of the {@link User}
     *
     * @param last New last-seen date to be set
     */
    public void setLast(final ZonedDateTime last) {
        this.last = last;
    }

    /**
     * Deletes the {@link User}
     */
    @Override
    public void delete() {
        net.cryptic_game.backend.data.user.Session.getByUser(this).forEach(TableModel::delete);
        super.delete();
    }

    /**
     * Compares an {@link Object} if it equals the {@link User}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link User} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(this.getVersion(), that.getVersion()) &&
                Objects.equals(this.getCreated(), that.getCreated()) &&
                Objects.equals(this.getLast(), that.getLast()) &&
                Objects.equals(this.getMail(), that.getMail()) &&
                Objects.equals(this.getUsername(), that.getUsername()) &&
                Objects.equals(this.getPasswordHash(), that.getPasswordHash());
    }

    /**
     * Hashes the {@link User} using {@link Objects} hash method
     *
     * @return Hash of the {@link User}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getVersion(), this.getCreated(), this.getLast(),
                this.getMail(), this.getUsername(), this.getPasswordHash());
    }
}
