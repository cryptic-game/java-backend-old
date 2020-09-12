package net.cryptic_game.backend.data.repositories.user;

import net.cryptic_game.backend.data.Constants;
import net.cryptic_game.backend.data.entities.user.Session;
import net.cryptic_game.backend.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findAllByUser(final User user);

    @Modifying
    @Transactional
    @Query("delete from Session where user = ?1 and expire < ?2")
    void deleteAllExpired(User user, OffsetDateTime dateTime);

    default Session createSession(final User user) {
        final OffsetDateTime now = OffsetDateTime.now();
        return this.save(new Session(user, UUID.randomUUID(), now.plus(Constants.EXPIRE), now));
    }
}
