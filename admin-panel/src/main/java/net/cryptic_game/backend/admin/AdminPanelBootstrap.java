package net.cryptic_game.backend.admin;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.admin.data.sql.entities.user.AdminUser;
import net.cryptic_game.backend.admin.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Key;
import java.util.Set;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "net.cryptic_game.backend.admin.data.sql")
public class AdminPanelBootstrap {

    public AdminPanelBootstrap(final AdminPanelConfig config,
                               final AdminUserRepository adminUserRepository) {
        if (adminUserRepository.findById(config.getAdminGitHubId()).isEmpty()) {
            final AdminUser user = new AdminUser();
            user.setId(config.getAdminGitHubId());
            user.setGroups(Set.of(Groups.ADMIN));
            adminUserRepository.save(user);
        }
    }

    @Bean
    ApiAuthenticationProvider authenticationProvider(final Key key) {
        return new AdminPanelAuthenticationProvider(key, Groups.values());
    }
}
