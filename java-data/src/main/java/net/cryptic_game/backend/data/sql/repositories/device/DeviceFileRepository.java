package net.cryptic_game.backend.data.sql.repositories.device;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.device.DeviceFile;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceFileRepository extends JpaRepository<DeviceFile, UUID> {

    List<DeviceFile> findAllByDevice(Device device);

    private DeviceFile create(final Device device, final String name, final String contents, final boolean isDirectory, final DeviceFile parentDir) {
        final DeviceFile file = new DeviceFile();
        file.setDevice(device);
        file.setName(name);
        file.setContent(contents);
        file.setDirectory(isDirectory);
        file.setParentDirectory(parentDir);

        return this.save(file);
    }

    default DeviceFile createFile(final Device device, final String name, final String contents, final DeviceFile parentDir) {
        return create(device, name, contents, false, parentDir);
    }

    default DeviceFile createDirectory(final Session session, final Device device, final String name, final DeviceFile parentDir) {
        return create(device, name, "", true, parentDir);
    }
}
