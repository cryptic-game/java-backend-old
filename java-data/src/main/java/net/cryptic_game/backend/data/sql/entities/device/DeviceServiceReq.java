package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModelAutoId;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.data.sql.entities.service.Service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a device service req entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_service_req")
public final class DeviceServiceReq extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "device_id") // updatable?
    private Device device;

    @Column(name = "allocated_cpu")
    private float allocatedCPU;

    @Column(name = "allocated_ram")
    private float allocatedRAM;

    @Column(name = "allocated_gpu")
    private float allocatedGPU;

    @Column(name = "allocated_disk")
    private float allocatedDisk;

    @Column(name = "allocated_network")
    private float allocatedNetwork;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceServiceReq} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("service_id", this.getService().getId())
                .add("allocated_cpu", this.getAllocatedCPU())
                .add("allocated_ram", this.getAllocatedRAM())
                .add("allocated_gpu", this.getAllocatedGPU())
                .add("allocated_disk", this.getAllocatedDisk())
                .add("allocated_network", this.getAllocatedNetwork())
                .build();
    }
}
