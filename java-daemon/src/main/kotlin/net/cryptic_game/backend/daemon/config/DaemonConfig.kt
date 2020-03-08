package net.cryptic_game.backend.daemon.config

import net.cryptic_game.backend.base.config.Config
import net.cryptic_game.backend.base.config.DefaultConfig

enum class DaemonConfig : DefaultConfig {
    // Ignore
    CONFIG;

    override fun iniConfig(config: Config) = Unit
}