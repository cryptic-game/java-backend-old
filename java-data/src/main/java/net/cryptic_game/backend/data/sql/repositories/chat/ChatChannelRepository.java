package net.cryptic_game.backend.data.sql.repositories.chat;

import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatChannelRepository extends JpaRepository<ChatChannel, UUID> {

    default ChatChannel create(final String name) {
        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setName(name);

        return this.save(chatChannel);
    }

    /*@Transactional
    @Modifying
    @Query("delete from ChatMessage as m where m.channel = ?1;" +
            "delete from ChatChannelAccess as ca where ca.channel = ?1")
    default void delete(ChatChannel channel) {
        deleteById(channel.getId());
    }*/
}
