package net.cryptic_game.backend.data.sql.repositories.device;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.device.DeviceAccess;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceAccessRepository extends JpaRepository<DeviceAccess, UUID> {

    @Query("select object (da) from DeviceAccess da where "
            + "da.user = ?2 and "
            + "da.device = ?1 and "
            + "da.valid = true and "
            + "da.expire > ?3")
    List<DeviceAccess> hasAccess(Device device, User user, OffsetDateTime now);

    List<DeviceAccess> getAllByUserAndExpireAfter(User user, OffsetDateTime now);

    default boolean hasAccess(Device device, User user) {
        return hasAccess(device, user, OffsetDateTime.now()).size() > 0 || device.getOwner().equals(user);
    }

    default DeviceAccess grantAccess(final Device device, final User user, final Duration duration) {
        final DeviceAccess access = new DeviceAccess();
        access.setUser(user);
        access.setDevice(device);
        access.setGranted(OffsetDateTime.now());
        access.setValid(true);
        access.setExpire(OffsetDateTime.now().plus(duration));

        return this.save(access);
    }
}
