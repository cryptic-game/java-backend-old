package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing a device workload entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_workload")
public class DeviceWorkload extends TableModel implements JsonSerializable, Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "device_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "performance_cpu", updatable = true, nullable = false)
    private float performanceCPU;

    @Column(name = "performance_gpu", updatable = true, nullable = false)
    private float performanceGPU;

    @Column(name = "performance_ram", updatable = true, nullable = false)
    private float performanceRAM;

    @Column(name = "performance_disk", updatable = true, nullable = false)
    private float performanceDisk;

    @Column(name = "performance_network", updatable = true, nullable = false)
    private float performanceNetwork;

    @Column(name = "usage_cpu", updatable = true, nullable = false)
    private float usageCPU;

    @Column(name = "usage_gpu", updatable = true, nullable = false)
    private float usageGPU;

    @Column(name = "usage_ram", updatable = true, nullable = false)
    private float usageRAM;

    @Column(name = "usage_disk", updatable = true, nullable = false)
    private float usageDisk;

    @Column(name = "usage_network", updatable = true, nullable = false)
    private float usageNetwork;

    /**
     * Returns the {@link Device} of the {@link DeviceWorkload}.
     *
     * @return the {@link Device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * Sets a new {@link Device} for the {@link DeviceWorkload}.
     *
     * @param device the new {@link Device} to be set
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Returns the performance of to CPU from the {@link DeviceWorkload}.
     *
     * @return the performance
     */
    public float getPerformanceCPU() {
        return this.performanceCPU;
    }

    /**
     * Sets a new performance for the CPU from the {@link DeviceWorkload}.
     *
     * @param performanceCPU the new performance to be set
     */
    public void setPerformanceCPU(final float performanceCPU) {
        this.performanceCPU = performanceCPU;
    }

    /**
     * Returns the performance of the GPU from the {@link DeviceWorkload}.
     *
     * @return the performance
     */
    public float getPerformanceGPU() {
        return this.performanceGPU;
    }

    /**
     * Sets a new performance for the GPU from the {@link DeviceWorkload}.
     *
     * @param performanceGPU the new performance to be set
     */
    public void setPerformanceGPU(final float performanceGPU) {
        this.performanceGPU = performanceGPU;
    }

    /**
     * Returns the performance of the RAM from the {@link DeviceWorkload}.
     *
     * @return the performance
     */
    public float getPerformanceRAM() {
        return this.performanceRAM;
    }

    /**
     * Sets a new performance for the RAM from the {@link DeviceWorkload}.
     *
     * @param performanceRAM the new performance to be set
     */
    public void setPerformanceRAM(final float performanceRAM) {
        this.performanceRAM = performanceRAM;
    }

    /**
     * Returns the performance of the Disk from the {@link DeviceWorkload}.
     *
     * @return the performance
     */
    public float getPerformanceDisk() {
        return this.performanceDisk;
    }

    /**
     * Sets a new performance for the RAM from the {@link DeviceWorkload}.
     *
     * @param performanceDisk the new performance to be set
     */
    public void setPerformanceDisk(final float performanceDisk) {
        this.performanceDisk = performanceDisk;
    }

    /**
     * Returns the performance of the Network from the {@link DeviceWorkload}.
     *
     * @return the performance
     */
    public float getPerformanceNetwork() {
        return this.performanceNetwork;
    }

    /**
     * Sets a new performance for the Network from the {@link DeviceWorkload}.
     *
     * @param performanceNetwork the new performance to be set
     */
    public void setPerformanceNetwork(final float performanceNetwork) {
        this.performanceNetwork = performanceNetwork;
    }

    /**
     * Returns the usage of the CPU from the {@link DeviceWorkload}.
     *
     * @return the usage
     */
    public float getUsageCPU() {
        return this.usageCPU;
    }

    /**
     * Sets a new usage for the CPU from the {@link DeviceWorkload}.
     *
     * @param usageCPU the new usage to be set
     */
    public void setUsageCPU(final float usageCPU) {
        this.usageCPU = usageCPU;
    }

    /**
     * Returns the usage of the GPU from the {@link DeviceWorkload}.
     *
     * @return the usage
     */
    public float getUsageGPU() {
        return this.usageGPU;
    }

    /**
     * Sets a new usage for the GPU from the {@link DeviceWorkload}.
     *
     * @param usageGPU the new usage to be set
     */
    public void setUsageGPU(final float usageGPU) {
        this.usageGPU = usageGPU;
    }

    /**
     * Returns the usage of the RAM from the {@link DeviceWorkload}.
     *
     * @return the usage
     */
    public float getUsageRAM() {
        return this.usageRAM;
    }

    /**
     * Sets a new usage for the RAM from the {@link DeviceWorkload}.
     *
     * @param usageRAM the new usage to be set
     */
    public void setUsageRAM(final float usageRAM) {
        this.usageRAM = usageRAM;
    }

    /**
     * Returns the usage of the Disk from the {@link DeviceWorkload}.
     *
     * @return the usage
     */
    public float getUsageDisk() {
        return this.usageDisk;
    }

    /**
     * Sets a new usage for the Disk from the {@link DeviceWorkload}.
     *
     * @param usageDisk the new usage to be set
     */
    public void setUsageDisk(final float usageDisk) {
        this.usageDisk = usageDisk;
    }

    /**
     * Returns the usage of the Network from the {@link DeviceWorkload}.
     *
     * @return the usage
     */
    public float getUsageNetwork() {
        return this.usageNetwork;
    }

    /**
     * Sets a new usage for the Network from the {@link DeviceWorkload}.
     *
     * @param usageNetwork the new usage to be set
     */
    public void setUsageNetwork(final float usageNetwork) {
        this.usageNetwork = usageNetwork;
    }

    /**
     * Compares an {@link Object} if it equals the {@link DeviceWorkload}.
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link DeviceWorkload} | False if it does not
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceWorkload workload = (DeviceWorkload) o;
        return Float.compare(workload.getPerformanceCPU(), getPerformanceCPU()) == 0
                && Float.compare(workload.getPerformanceGPU(), getPerformanceGPU()) == 0
                && Float.compare(workload.getPerformanceRAM(), getPerformanceRAM()) == 0
                && Float.compare(workload.getPerformanceDisk(), getPerformanceDisk()) == 0
                && Float.compare(workload.getPerformanceNetwork(), getPerformanceNetwork()) == 0
                && Float.compare(workload.getUsageCPU(), getUsageCPU()) == 0
                && Float.compare(workload.getUsageGPU(), getUsageGPU()) == 0
                && Float.compare(workload.getUsageRAM(), getUsageRAM()) == 0
                && Float.compare(workload.getUsageDisk(), getUsageDisk()) == 0
                && Float.compare(workload.getUsageNetwork(), getUsageNetwork()) == 0
                && Objects.equals(getDevice(), workload.getDevice());
    }

    /**
     * Hashes the {@link DeviceWorkload} using {@link Objects} hash method.
     *
     * @return Hash of the {@link DeviceWorkload}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getPerformanceCPU(), getPerformanceGPU(), getPerformanceRAM(),
                getPerformanceDisk(), getPerformanceNetwork(), getUsageCPU(), getUsageGPU(), getUsageRAM(),
                getUsageDisk(), getUsageNetwork());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceWorkload} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("device", this.getDevice().getId())
                .add("performance_cpu", this.getPerformanceCPU())
                .add("performance_gpu", this.getPerformanceGPU())
                .add("performance_ram", this.getPerformanceRAM())
                .add("performance_disk", this.getPerformanceDisk())
                .add("performance_network", this.getPerformanceNetwork())
                .add("usage_cpu", this.getUsageCPU())
                .add("usage_gpu", this.getUsageGPU())
                .add("usage_ram", this.getUsageRAM())
                .add("usage_disk", this.getUsageDisk())
                .add("usage_network", this.getUsageNetwork())
                .build();
    }
}
