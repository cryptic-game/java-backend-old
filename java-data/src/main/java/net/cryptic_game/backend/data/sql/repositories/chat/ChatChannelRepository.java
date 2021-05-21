package net.cryptic_game.backend.data.sql.repositories.chat;

import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatChannelRepository extends JpaRepository<ChatChannel, UUID> {

    default ChatChannel create(final String name) {
        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setName(name);

        return this.save(chatChannel);
    }
}
