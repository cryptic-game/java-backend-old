package net.cryptic_game.backend.data.redis.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.data.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

/**
 * Entity representing a session entry in the database.
 *
 * @since 0.3.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "session", timeToLive = Constants.SESSION_EXPIRE)
public final class Session {

    @Id
    private UUID id;

    private UUID userId;
}
