package net.cryptic_game.backend.data.entities.device;

import lombok.Data;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device_hardware_manufacturer")
@Data
public final class DeviceHardwareManufacturer extends TableModelAutoId {

    @Column(name = "name", updatable = false, nullable = false)
    private String name;
}
