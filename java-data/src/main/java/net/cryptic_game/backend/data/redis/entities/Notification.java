package net.cryptic_game.backend.data.redis.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.data.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "notification", timeToLive = Constants.NOTIFICATION_EXPIRE)
public class Notification {

    @Id
    private UUID id;
    private String topic;
    private String data;
}
