package net.cryptic_game.backend.admin;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.data.Group;
import net.cryptic_game.backend.data.sql.entities.user.AdminUser;
import net.cryptic_game.backend.data.sql.repositories.user.AdminUserRepository;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
public class AdminPanelBootstrap {

    public AdminPanelBootstrap(final AdminPanelConfig config,
                               final AdminUserRepository adminUserRepository) {
        if (adminUserRepository.findById(config.getAdminGitHubId()).isEmpty()) {
            final AdminUser user = new AdminUser();
            user.setId(config.getAdminGitHubId());
            user.setGroups(Set.of(Group.ADMIN));
            adminUserRepository.save(user);
        }
    }
}
