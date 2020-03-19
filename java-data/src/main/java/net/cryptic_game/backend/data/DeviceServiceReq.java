package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "device_service_req")
public class DeviceServiceReq extends TableModelId {


    @ManyToOne
    @Column(name = "service", updatable = true, nullable = true)
    @Type(type = "uuid-char")
    private Service service;

    @ManyToOne
    @Column(name = "device", updatable = true, nullable = true) // updatable?
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "allocated_cpu", updatable = true, nullable = true)
    private float allocated_cpu;

    @Column(name = "allocated_ram", updatable = true, nullable = true)
    private float allocated_ram;

    @Column(name = "allocated_gpu", updatable = true, nullable = true)
    private float allocated_gpu;

    @Column(name = "allocated_disk", updatable = true, nullable = true)
    private float allocated_disk;

    @Column(name = "allocated_network", updatable = true, nullable = true)
    private float allocated_network;

    public Service getService() {
        return service;
    }

    public void setService_uuid(Service service_uuid) {
        this.service = service;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(String source_uuid) {
        this.device = device;
    }

    public float getAllocated_cpu() {
        return allocated_cpu;
    }

    public void setAllocated_cpu(float allocated_cpu) {
        this.allocated_cpu = allocated_cpu;
    }

    public float getAllocated_ram() {
        return allocated_ram;
    }

    public void setAllocated_ram(float allocated_ram) {
        this.allocated_ram = allocated_ram;
    }

    public float getAllocated_gpu() {
        return allocated_gpu;
    }

    public void setAllocated_gpu(float allocated_gpu) {
        this.allocated_gpu = allocated_gpu;
    }

    public float getAllocated_disk() {
        return allocated_disk;
    }

    public void setAllocated_disk(float allocated_disk) {
        this.allocated_disk = allocated_disk;
    }

    public float getAllocated_network() {
        return allocated_network;
    }

    public void setAllocated_network(float allocated_network) {
        this.allocated_network = allocated_network;
    }


    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add( "device", this.getDevice().getId())
                .add("service", this.getService().getId())
                .add("allocated_cpu", this.getAllocated_cpu())
                .add("allocated_ram", this.getAllocated_ram())
                .add("allocated_gpu", this.getAllocated_gpu())
                .add("allocated_disk", this.getAllocated_disk())
                .add("allocated_network", this.getAllocated_disk())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceServiceReq that = (DeviceServiceReq) o;
        return Float.compare(that.getAllocated_cpu(), getAllocated_cpu()) == 0 &&
                Float.compare(that.getAllocated_ram(), getAllocated_ram()) == 0 &&
                Float.compare(that.getAllocated_gpu(), getAllocated_gpu()) == 0 &&
                Float.compare(that.getAllocated_disk(), getAllocated_disk()) == 0 &&
                Float.compare(that.getAllocated_network(), getAllocated_network()) == 0 &&
                Objects.equals(getService(), that.getService()) &&
                Objects.equals(getDevice(), that.getDevice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getService(), getDevice(), getAllocated_cpu(), getAllocated_ram(), getAllocated_gpu(), getAllocated_disk(), getAllocated_network());
    }


}
