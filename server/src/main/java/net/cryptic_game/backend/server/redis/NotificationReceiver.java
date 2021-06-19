package net.cryptic_game.backend.server.redis;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.redis.entities.Notification;
import net.cryptic_game.backend.data.redis.repositories.NotificationRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.server.api.handler.websocket.WebsocketApiContext;
import net.cryptic_game.backend.server.api.handler.websocket.WebsocketApiInitializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationReceiver {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final WebsocketApiInitializer websocketApiInitializer;
    private final NotificationRepository notificationRepository;

    public void receiveMessage(final String message) {
        if (message.length() != 73) {
            log.warn("Invalid notification package received through pub/sub: {}", message);
            return;
        }

        try {
            final UUID userId = UUID.fromString(message.substring(0, 36));
            final Set<WebsocketApiContext> contexts = this.websocketApiInitializer.getContexts().stream()
                    .filter(ctx -> ctx.get(User.class).map(user -> user.getId().equals(userId)).orElse(false))
                    .collect(Collectors.toSet());

            if (contexts.isEmpty()) return;

            final UUID notificationId = UUID.fromString(message.substring(37, 73));
            final Optional<Notification> notification = this.notificationRepository.findById(notificationId);
            if (notification.isEmpty()) {
                log.warn("Invalid notification package received through pub/sub: Notification {} not found", notificationId);
                return;
            }


            notification.ifPresent(n -> {
                try {
                    final String notificationString = JsonBuilder
                            .create("status", JsonBuilder.create("code", 900).add("name", "Notification"))
                            .add("topic", n.getTopic())
                            .add("data", JsonParser.parseString(n.getData()))
                            .toString();

                    Flux.fromIterable(contexts)
                            .flatMap(context -> context.getOutbound().sendString(Mono.just(notificationString), CHARSET))
                            .subscribe();
                } catch (JsonParseException e) {
                    log.warn("Invalid notification data received through pub/sub: {} {} {}", n.getId(), n.getTopic(), n.getData());
                }
            });

        } catch (IllegalArgumentException e) {
            log.warn("Invalid notification package received through pub/sub: {}", message);
        }
    }
}
