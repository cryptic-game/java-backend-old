package net.cryptic_game.backend.admin.data.sql.repositories.user;

import net.cryptic_game.backend.admin.data.sql.entities.user.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
}
