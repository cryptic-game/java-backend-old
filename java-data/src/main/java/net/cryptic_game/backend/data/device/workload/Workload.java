package net.cryptic_game.backend.data.device.workload;

import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.cryptic_game.backend.base.sql.TableModel;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.device.Device;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "device_workload")
public class Workload extends TableModel implements Serializable {

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

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public float getPerformanceCPU() {
        return this.performanceCPU;
    }

    public void setPerformanceCPU(final float performanceCPU) {
        this.performanceCPU = performanceCPU;
    }

    public float getPerformanceGPU() {
        return this.performanceGPU;
    }

    public void setPerformanceGPU(final float performanceGPU) {
        this.performanceGPU = performanceGPU;
    }

    public float getPerformanceRAM() {
        return this.performanceRAM;
    }

    public void setPerformanceRAM(final float performanceRAM) {
        this.performanceRAM = performanceRAM;
    }

    public float getPerformanceDisk() {
        return this.performanceDisk;
    }

    public void setPerformanceDisk(final float performanceDisk) {
        this.performanceDisk = performanceDisk;
    }

    public float getPerformanceNetwork() {
        return this.performanceNetwork;
    }

    public void setPerformanceNetwork(final float performanceNetwork) {
        this.performanceNetwork = performanceNetwork;
    }

    public float getUsageCPU() {
        return this.usageCPU;
    }

    public void setUsageCPU(final float usageCPU) {
        this.usageCPU = usageCPU;
    }

    public float getUsageGPU() {
        return this.usageGPU;
    }

    public void setUsageGPU(final float usageGPU) {
        this.usageGPU = usageGPU;
    }

    public float getUsageRAM() {
        return this.usageRAM;
    }

    public void setUsageRAM(final float usageRAM) {
        this.usageRAM = usageRAM;
    }

    public float getUsageDisk() {
        return this.usageDisk;
    }

    public void setUsageDisk(final float usageDisk) {
        this.usageDisk = usageDisk;
    }

    public float getUsageNetwork() {
        return this.usageNetwork;
    }

    public void setUsageNetwork(final float usageNetwork) {
        this.usageNetwork = usageNetwork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workload workload = (Workload) o;
        return Float.compare(workload.getPerformanceCPU(), getPerformanceCPU()) == 0 &&
                Float.compare(workload.getPerformanceGPU(), getPerformanceGPU()) == 0 &&
                Float.compare(workload.getPerformanceRAM(), getPerformanceRAM()) == 0 &&
                Float.compare(workload.getPerformanceDisk(), getPerformanceDisk()) == 0 &&
                Float.compare(workload.getPerformanceNetwork(), getPerformanceNetwork()) == 0 &&
                Float.compare(workload.getUsageCPU(), getUsageCPU()) == 0 &&
                Float.compare(workload.getUsageGPU(), getUsageGPU()) == 0 &&
                Float.compare(workload.getUsageRAM(), getUsageRAM()) == 0 &&
                Float.compare(workload.getUsageDisk(), getUsageDisk()) == 0 &&
                Float.compare(workload.getUsageNetwork(), getUsageNetwork()) == 0 &&
                Objects.equals(getDevice(), workload.getDevice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getPerformanceCPU(), getPerformanceGPU(), getPerformanceRAM(), getPerformanceDisk(), getPerformanceNetwork(), getUsageCPU(), getUsageGPU(), getUsageRAM(), getUsageDisk(), getUsageNetwork());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("device", this.getDevice().getId())
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
