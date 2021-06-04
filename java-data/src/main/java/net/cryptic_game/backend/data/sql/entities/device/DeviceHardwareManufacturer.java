package net.cryptic_game.backend.data.sql.entities.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_hardware_manufacturer")
public final class DeviceHardwareManufacturer extends TableModelAutoId {

    @Column(name = "name", updatable = false, nullable = false)
    private String name;
}
