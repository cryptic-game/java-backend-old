package net.cryptic_game.backend.data.redis.repositories;

import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface SessionRepository extends CrudRepository<Session, UUID> {

    default Session createSession(final User user) {
        final OffsetDateTime now = OffsetDateTime.now();
        return this.save(new Session(UUID.randomUUID(), user.getId()));
    }
}
