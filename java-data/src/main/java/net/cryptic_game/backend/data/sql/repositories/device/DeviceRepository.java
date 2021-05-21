package net.cryptic_game.backend.data.sql.repositories.device;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    default Device create(final String name, final User owner, final boolean poweredOn) {
        Device device = new Device();
        device.setName(name);
        device.setOwner(owner);
        device.setPoweredOn(poweredOn);

        return this.save(device);
    }
}
