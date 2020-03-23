package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.service.Service;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device_service_req")
public class DeviceServiceReq extends TableModelId {

    @ManyToOne
    @JoinColumn(name = "service", updatable = true, nullable = true)
    @Type(type = "uuid-char")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "device", updatable = true, nullable = true) // updatable?
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "allocated_cpu", updatable = true, nullable = true)
    private float allocatedCPU;

    @Column(name = "allocated_ram", updatable = true, nullable = true)
    private float allocatedRAM;

    @Column(name = "allocated_gpu", updatable = true, nullable = true)
    private float allocatedGPU;

    @Column(name = "allocated_disk", updatable = true, nullable = true)
    private float allocatedDisk;

    @Column(name = "allocated_network", updatable = true, nullable = true)
    private float allocatedNetwork;

    public Service getService() {
        return this.service;
    }

    public void setService(final Service service) {
        this.service = service;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public float getAllocatedCPU() {
        return this.allocatedCPU;
    }

    public void setAllocatedCPU(final float allocatedCPU) {
        this.allocatedCPU = allocatedCPU;
    }

    public float getAllocatedRAM() {
        return this.allocatedRAM;
    }

    public void setAllocatedRAM(final float allocatedRAM) {
        this.allocatedRAM = allocatedRAM;
    }

    public float getAllocatedGPU() {
        return this.allocatedGPU;
    }

    public void setAllocatedGPU(final float allocatedGPU) {
        this.allocatedGPU = allocatedGPU;
    }

    public float getAllocatedDisk() {
        return this.allocatedDisk;
    }

    public void setAllocatedDisk(final float allocatedDisk) {
        this.allocatedDisk = allocatedDisk;
    }

    public float getAllocatedNetwork() {
        return this.allocatedNetwork;
    }

    public void setAllocatedNetwork(final float allocatedNetwork) {
        this.allocatedNetwork = allocatedNetwork;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device", this.getDevice().getId())
                .add("service", this.getService().getId())
                .add("allocated_cpu", this.getAllocatedCPU())
                .add("allocated_ram", this.getAllocatedRAM())
                .add("allocated_gpu", this.getAllocatedGPU())
                .add("allocated_disk", this.getAllocatedDisk())
                .add("allocated_network", this.getAllocatedNetwork())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceServiceReq that = (DeviceServiceReq) o;
        return Float.compare(that.getAllocatedCPU(), getAllocatedCPU()) == 0 &&
                Float.compare(that.getAllocatedRAM(), getAllocatedRAM()) == 0 &&
                Float.compare(that.getAllocatedGPU(), getAllocatedGPU()) == 0 &&
                Float.compare(that.getAllocatedDisk(), getAllocatedDisk()) == 0 &&
                Float.compare(that.getAllocatedNetwork(), getAllocatedNetwork()) == 0 &&
                Objects.equals(getService(), that.getService()) &&
                Objects.equals(getDevice(), that.getDevice()) &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getService(), getDevice(), getAllocatedCPU(), getAllocatedRAM(), getAllocatedGPU(), getAllocatedDisk(), getAllocatedNetwork());
    }
}
