package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.service.Service;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device_service_req")
public class DeviceServiceReq extends TableModelAutoId {

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

    /**
     * Returns the {@link Service} where the {@link DeviceServiceReq} is located
     *
     * @return the {@link Service}
     */
    public Service getService() {
        return this.service;
    }

    /**
     * Sets a new {@link Service} for the {@link DeviceServiceReq}
     *
     * @param service the new {@link Service} to be set
     */
    public void setService(final Service service) {
        this.service = service;
    }

    /**
     * Returns the {@link Device} where the {@link Device} is located
     *
     * @return the {@link Device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets a new {@link Device} for the {@link Device}
     *
     * @param device the new {@link Device} to be set
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the allocated CPU
     *
     * @return the allocated CPU
     */
    public float getAllocatedCPU() {
        return this.allocatedCPU;
    }

    /**
     * Sets the allocated CPU
     *
     * @param allocatedCPU the new allocated CPU to be set
     */
    public void setAllocatedCPU(final float allocatedCPU) {
        this.allocatedCPU = allocatedCPU;
    }

    /**
     * Returns the allocated RAM
     *
     * @return the allocated RAM
     */
    public float getAllocatedRAM() {
        return this.allocatedRAM;
    }

    /**
     * Sets the allocated RAM
     *
     * @param allocatedRAM the new allocated RAM to be set
     */
    public void setAllocatedRAM(final float allocatedRAM) {
        this.allocatedRAM = allocatedRAM;
    }

    /**
     * Returns the allocated GPU
     *
     * @return the allocated GPU
     */
    public float getAllocatedGPU() {
        return this.allocatedGPU;
    }

    /**
     * Sets the allocated GPU
     *
     * @param allocatedGPU the new allocated GPU to be set
     */
    public void setAllocatedGPU(final float allocatedGPU) {
        this.allocatedGPU = allocatedGPU;
    }

    /**
     * Returns the allocated Disk
     *
     * @return the allocated Disk
     */
    public float getAllocatedDisk() {
        return this.allocatedDisk;
    }

    /**
     * Sets the allocated Disk
     *
     * @param allocatedDisk the new allocated Disk to be set
     */
    public void setAllocatedDisk(final float allocatedDisk) {
        this.allocatedDisk = allocatedDisk;
    }

    /**
     * Returns the allocated Network
     *
     * @return the allocated Network
     */
    public float getAllocatedNetwork() {
        return this.allocatedNetwork;
    }

    /**
     * Sets the allocated Network
     *
     * @param allocatedNetwork the new allocated Network to be set
     */
    public void setAllocatedNetwork(final float allocatedNetwork) {
        this.allocatedNetwork = allocatedNetwork;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceServiceReq} information
     *
     * @return The generated {@link JsonObject}
     */
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

    /**
     * Compares an {@link Object} if it equals the {@link DeviceServiceReq}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceServiceReq} | False if it does not
     */
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

    /**
     * Hashes the {@link DeviceServiceReq} using {@link Objects} hash method
     *
     * @return Hash of the {@link DeviceServiceReq}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getService(), getDevice(), getAllocatedCPU(), getAllocatedRAM(), getAllocatedGPU(), getAllocatedDisk(), getAllocatedNetwork());
    }
}
