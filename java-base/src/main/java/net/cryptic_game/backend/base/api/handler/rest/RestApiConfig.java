package net.cryptic_game.backend.base.api.handler.rest;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
final class RestApiConfig {

    @Value("${REST_API_PATH:api}")
    private String path;
}
