package net.cryptic_game.backend.admin;

import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter
public class AdminPanelConfig {

    @Value("${ADMIN_GITHUB_ID:22866859}")
    private long adminGitHubId;
}
