package net.cryptic_game.backend.data.sql.repositories.user;

import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    default User createUser(final String username, final String password) {
        final OffsetDateTime now = OffsetDateTime.now();
        return this.save(new User(username, SecurityUtils.hash(password), now, now));
    }
}
