package net.cryptic_game.backend.data.sql.repositories.user;

import net.cryptic_game.backend.data.sql.entities.user.UserSuspension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserSuspensionRepository extends JpaRepository<UserSuspension, UUID> {

    Page<UserSuspension> findAllByUserId(UUID userId, Pageable pageable);

    @Query("select object(us) from UserSuspension as us where us.user.id = ?1 " +
            "and us.expires > current_timestamp and us.id not in " +
            "(select usr.userSuspension.id from UserSuspensionRevoked as usr)")
    List<UserSuspension> getActiveSuspensionsByUserId(UUID userId);

}
