package net.cryptic_game.backend.data.user;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.user.session.SessionWrapper;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Contains management methods for {@link User}
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
     * Create a {@link User} with the given user data
     *
     * @param name     The name of the new user
     * @param mail     The mail address of the new user
     * @param password The password of the new user
     * @return The instance of the created {@link User}
     */
    public static User registerUser(final String name, final String mail, final String password) {
        final LocalDateTime now = LocalDateTime.now();

        final User user = new User();
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
     * Fetches the {@link User} with the given id
     *
     * @param id The id of the User
     * @return The instance of the fetched {@link User} if it exists | null if the {@link User} does not exist
     */
    public static User getById(final UUID id) {
        final Session session = sqlConnection.openSession();
        final User user = session.find(User.class, id);
        session.close();
        return user;
    }

    /**
     * Fetches the {@link User} with the give name
     *
     * @param name The name of the user
     * @return The instance of the fetched {@link User} if it exists | null if the {@link User} does not exist
     */
    public static User getByName(final String name) {
        final Session session = sqlConnection.openSession();
        final List<User> users = session
                .createQuery("select object (u) from User as u where u.name = :name", User.class)
                .setParameter("name", name)
                .getResultList();
        session.close();

        if (!users.isEmpty()) return users.get(0);
        return null;
    }

    public static void setPassword(final User user, final String newPassword) {
        user.setPasswordHash(SecurityUtils.hash(newPassword));
    }

    public static boolean verifyPassword(final User user, final String input) {
        return SecurityUtils.verify(input, user.getPasswordHash());
    }

    public static void setLastToCurrentTime(final User user) {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        user.setLast(LocalDateTime.now());
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    public static void deleteUser(final User user) {
        SessionWrapper.deleteSessions(user);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
        session.close();
    }
}
