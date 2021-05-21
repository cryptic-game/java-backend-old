package net.cryptic_game.backend.daemon.redis;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.data.redis.entities.Notification;
import net.cryptic_game.backend.data.redis.repositories.NotificationRepository;
import net.cryptic_game.backend.endpoints.NotificationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String TOPIC = "notifications";
    private final StringRedisTemplate redisTemplate;
    private final NotificationRepository notificationRepository;

    public void sendNotification(final UUID userId, final String topic, final JsonElement data) {
        this.redisTemplate.convertAndSend(TOPIC, userId + ":" + this.notificationRepository.save(new Notification(UUID.randomUUID(), topic, data.toString())).getId());
    }
}
