package net.cryptic_game.backend.data.repositories.user;

import net.cryptic_game.backend.data.entities.user.Session;
import net.cryptic_game.backend.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    private static final Duration EXPIRE = Duration.ofDays(14);

    List<Session> findAllByUser(final User user);

    default Session createSession(final User user) {
        final OffsetDateTime now = OffsetDateTime.now();
        return this.save(new Session(user, UUID.randomUUID(), now.plus(EXPIRE), now));
    }
}
