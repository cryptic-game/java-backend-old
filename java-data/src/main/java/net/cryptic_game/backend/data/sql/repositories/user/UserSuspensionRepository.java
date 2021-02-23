package net.cryptic_game.backend.data.sql.repositories.user;

import net.cryptic_game.backend.data.sql.entities.user.UserSuspension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface UserSuspensionRepository extends JpaRepository<UserSuspension, UUID> {

    Page<UserSuspension> findAllByUserId(UUID userId, Pageable pageable);

    List<UserSuspension> findAllByUserIdAndExpiresAfter(UUID userId, OffsetDateTime expires);
}
