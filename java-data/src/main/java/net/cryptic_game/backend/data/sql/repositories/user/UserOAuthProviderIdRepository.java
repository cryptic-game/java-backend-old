package net.cryptic_game.backend.data.sql.repositories.user;

import net.cryptic_game.backend.data.sql.entities.user.UserOAuth2ProviderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOAuthProviderIdRepository extends JpaRepository<UserOAuth2ProviderId, UserOAuth2ProviderId.ProviderUserId> {
}
