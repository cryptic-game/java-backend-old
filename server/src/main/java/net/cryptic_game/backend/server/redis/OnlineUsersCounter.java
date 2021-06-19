package net.cryptic_game.backend.server.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineUsersCounter {

    private final RedisTemplate<String, Object> redisTemplate;

    public void increaseOnlineCount() {
        redisTemplate.opsForValue().increment("online");
    }

    public void decreaseOnlineCount() {
        redisTemplate.opsForValue().decrement("online");
    }

    public int getOnlineCount() {
        final Object online = redisTemplate.opsForValue().get("online");
        if (online == null) return 0;
        assert online instanceof String;
        return Integer.parseInt((String) online);
    }
}
