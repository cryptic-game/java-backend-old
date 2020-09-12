package net.cryptic_game.backend.data.repositories.chat;

import net.cryptic_game.backend.data.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.entities.chat.ChatChannelAccess;
import net.cryptic_game.backend.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatChannelAccessRepository extends JpaRepository<ChatChannelAccess, UUID> {

    Optional<ChatChannelAccess> findByUserAndChannel(User user, ChatChannel channel);

    List<ChatChannelAccess> findAllByChannel(ChatChannel channel);

    @Query("select ca.user from ChatChannelAccess as ca where ca.channel = ?1")
    List<User> getMembers(ChatChannel channel);

    @Query("select ca.channel from ChatChannelAccess as ca where ca.user = ?1")
    List<ChatChannel> getChannels(User user);

    void deleteAllByChannel(ChatChannel channel);

    void deleteAllByUser(User user);
}
