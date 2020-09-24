package net.cryptic_game.backend.data.sql.repositories.chat;

import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.sql.entities.chat.ChatMessage;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("select object (m) from ChatMessage m where m.channel = ?1 and (m.user = ?2 or m.target = ?2 or m.target = null)")
    List<ChatMessage> getMessages(ChatChannel channel, User user);

    @Query("select object (m) from ChatMessage m where m.channel = ?1 "
            + "and (m.user = ?2 or m.target = ?2 or m.target = null) "
            + "order by m.timestamp desc")
    List<ChatMessage> getMessages(ChatChannel chatChannel, User user, Pageable pageable);

    @Transactional
    @Modifying
    void deleteAllByChannel(ChatChannel channel);

    default ChatMessage create(final User user, final ChatChannel channel, final String text, final User target) {
        final ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setChannel(channel);
        message.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        message.setText(text);
        message.setTarget(target);

        return this.save(message);
    }
}
