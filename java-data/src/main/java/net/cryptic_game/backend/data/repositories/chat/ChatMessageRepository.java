package net.cryptic_game.backend.data.repositories.chat;

import net.cryptic_game.backend.data.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.entities.chat.ChatMessage;
import net.cryptic_game.backend.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("select object (m) from ChatMessage m where m.channel = ?1 and (m.user = ?2 or m.target = ?2 or m.target = null)")
    List<ChatMessage> getMessages(ChatChannel channel, User user);

    @Query("select object (m) from ChatMessage m where m.channel = ?1 "
            + "and (m.user = ?2 or m.target = ?2 or m.target = null) "
            + "order by m.timestamp desc")
    List<ChatMessage> getMessages(ChatChannel chatChannel, User user, Pageable pageable);

    void deleteAllByChannel(ChatChannel channel);
}
