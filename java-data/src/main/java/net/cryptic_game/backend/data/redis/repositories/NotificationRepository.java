package net.cryptic_game.backend.data.redis.repositories;

import net.cryptic_game.backend.data.redis.entities.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, UUID> {
}
