package net.cryptic_game.backend.data.sql.entities.device;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModel;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing a device workload entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "device_workload")
public final class DeviceWorkload extends TableModel implements JsonSerializable, Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "device_id", updatable = false, nullable = false)
    private Device device;

    @Column(name = "performance_cpu", nullable = false)
    private float performanceCPU;

    @Column(name = "performance_gpu", nullable = false)
    private float performanceGPU;

    @Column(name = "performance_ram", nullable = false)
    private float performanceRAM;

    @Column(name = "performance_disk", nullable = false)
    private float performanceDisk;

    @Column(name = "performance_network", nullable = false)
    private float performanceNetwork;

    @Column(name = "usage_cpu", nullable = false)
    private float usageCPU;

    @Column(name = "usage_gpu", nullable = false)
    private float usageGPU;

    @Column(name = "usage_ram", nullable = false)
    private float usageRAM;

    @Column(name = "usage_disk", nullable = false)
    private float usageDisk;

    @Column(name = "usage_network", nullable = false)
    private float usageNetwork;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link DeviceWorkload} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("device_id", this.getDevice().getId())
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
