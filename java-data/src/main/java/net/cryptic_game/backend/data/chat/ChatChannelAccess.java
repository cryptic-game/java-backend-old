package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a chat channel access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_channel_access")
@Data
@Slf4j
public class ChatChannelAccess extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private ChatChannel channel;

    /**
     * Joins a {@link User} to a {@link} Channel and returns the {@link ChatChannelAccess}.
     *
     * @param user        the {@link User} who join
     * @param channel     the {@link ChatChannel} where to join
     * @param notifyUsers the {@link ApiClient}s as {@link Set} which {@link ApiClient}s to notify
     * @return the resulting {@link ChatChannelAccess}
     */
    public static ChatChannelAccess join(final User user, final ChatChannel channel, final Set<ApiClient> notifyUsers) {
        ChatChannelAccess channelAccess = new ChatChannelAccess();
        channelAccess.setUser(user);
        channelAccess.setChannel(channel);

        channelAccess.saveOrUpdate();
        return channelAccess;
    }

    /**
     * The {@link User} leaves a {@link ChatChannel}.
     *
     * @param user        the {@link User} who leaves the {@link ChatChannel}
     * @param channel     the leaved {@link ChatChannel}
     * @param notifyUsers the {@link ApiClient}s as {@link Set} which {@link ApiClient}s to notify
     * @return True if successful | otherwise false
     */
    public static boolean leave(final User user, final ChatChannel channel, final Set<ApiClient> notifyUsers) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            sqlSession
                    .createQuery("delete from ChannelAccess ca where ca.channel = :channel and ca.user = :user")
                    .setParameter("channel", channel)
                    .setParameter("user", user);
            return true;
        } catch (HibernateException e) {
            log.error("The user wasn't found in the channel. Therefore he can't leave the channel", e);
            return false;
        }
    }

    /**
     * Returns a {@link List} of {@link User}s, who are in a {@link ChatChannel}.
     *
     * @param channel the {@link ChatChannel} where to get the {@link User}s from
     * @return the {@link List} of {@link User}s in the {@link ChatChannel}
     */
    public static List<User> getMembers(final ChatChannel channel) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession
                    .createQuery("select object (u) from User u where u.id in (select ca.user from ChannelAccess ca where ca.channel = :channel)", User.class)
                    .setParameter("channel", channel)
                    .getResultList();
        }
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatChannelAccess} information.
     *
     * @return The generated {@link JsonObject}
     */
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user_id", this.getUser().getId())
                .add("channel_id", this.getChannel().getId())
                .build();
    }
}

