package net.cryptic_game.backend.data.Chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "channel_access")
public class ChannelAccess extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Channel channel;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static ChannelAccess join(User user, Channel channel) {
        Session sqlSession = sqlConnection.openSession();

        ChannelAccess channelAccess = new ChannelAccess();
        channelAccess.setUser(user);
        channelAccess.setChannel(channel);

        sqlSession.beginTransaction();
        sqlSession.save(channelAccess);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return channelAccess;
    }

    public static boolean leave(User user, Channel channel) {
        try (Session sqlSession = sqlConnection.openSession()) {
            sqlSession
                    .createQuery("delete from ChannelAccess ca where ca.channel = :channel and ca.user = :user")
                    .setParameter("channel", channel)
                    .setParameter("user", user);
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    public static List<User> getMembers(final Channel channel) {

        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (u) from User u where u.id in (select ca.user from ChannelAccess ca where ca.channel = :channel)", User.class)
                    .setParameter("channel", channel)
                    .getResultList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelAccess that = (ChannelAccess) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getChannel(), that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getChannel());
    }

    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("user_id", this.getUser().getId())
                .add("channel_id", this.getChannel().getId())
                .build();
    }
}

