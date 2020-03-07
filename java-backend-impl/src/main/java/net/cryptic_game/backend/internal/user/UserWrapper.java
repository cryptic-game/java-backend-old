package net.cryptic_game.backend.internal.user;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.internal.user.session.SessionWrapper;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Contains management methods for {@link UserImpl}
 *
 * @since 0.3.0
 */
public class UserWrapper {

    private static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    /**
     * Empty constructor since the class only contains static methods
     */
    public UserWrapper() {
    }

    /**
     * Create a {@link UserImpl} with the given user data
     *
     * @param name     The name of the new user
     * @param mail     The mail address of the new user
     * @param password The password of the new user
     * @return The instance of the created {@link UserImpl}
     */
    public static UserImpl registerUser(final String name, final String mail, final String password) {
        final LocalDateTime now = LocalDateTime.now();

        final UserImpl user = new UserImpl();
        user.setName(name);
        user.setMail(mail);
        user.setCreated(now);
        user.setLast(now);

        setPassword(user, password);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
        return user;
    }

    /**
     * Fetches the {@link UserImpl} with the given id
     *
     * @param id The id of the User
     * @return The instance of the fetched {@link UserImpl} if it exists | null if the {@link UserImpl} does not exist
     */
    public static UserImpl getById(final UUID id) {
        final Session session = sqlConnection.openSession();
        final UserImpl user = session.find(UserImpl.class, id);
        session.close();
        return user;
    }

    /**
     * Fetches the {@link UserImpl} with the give name
     *
     * @param name The name of the user
     * @return The instance of the fetched {@link UserImpl} if it exists | null if the {@link UserImpl} does not exist
     */
    public static UserImpl getByName(final String name) {
        final Session session = sqlConnection.openSession();
        final List<UserImpl> users = session
                .createQuery("select object (u) from User as u where u.name = :name", UserImpl.class)
                .setParameter("name", name)
                .getResultList();
        session.close();

        if (!users.isEmpty()) return users.get(0);
        return null;
    }

    /**
     * Update the password of a given {@link UserImpl}
     *
     * @param user        The {@link UserImpl} whose password is to be updated
     * @param newPassword The new password
     */
    public static void setPassword(final UserImpl user, final String newPassword) {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        user.setPasswordHash(SecurityUtils.hash(newPassword));
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Verify
     *
     * @param user  The {@link UserImpl} whose password is to be verified
     * @param input The user's alleged password
     * @return true if the password is correct | false if the password is not correct
     */
    public static boolean verifyPassword(final UserImpl user, final String input) {
        return SecurityUtils.verify(input, user.getPasswordHash());
    }

    /**
     * Set the last-active timestamp of the given {@link UserImpl} to the current timestamp of the system
     *
     * @param user The {@link UserImpl} whose last-active timestamp is to be updated
     */
    public static void setLastToCurrentTime(final UserImpl user) {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        user.setLast(LocalDateTime.now());
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * @param user
     */
    public static void deleteUser(final UserImpl user) {
        SessionWrapper.deleteSessions(user);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
        session.close();
    }
}
