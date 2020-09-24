package net.cryptic_game.backend.data.sql.repositories.user;

import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {

    List<UserSetting> findAllByKeyUser(User user);

    Optional<UserSetting> findByKeyUserAndKeyKey(User user, String key);
}
