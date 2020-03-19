package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device_hardware")
public class DeviceHardware extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "element_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private DeviceHardwareElement element;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", updatable = false, nullable = false)
    private HardwareType type;

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public DeviceHardwareElement getElement() {
        return this.element;
    }

    public void setElement(final DeviceHardwareElement element) {
        this.element = element;
    }

    public HardwareType getType() {
        return this.type;
    }

    public void setType(final HardwareType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceHardware hardware = (DeviceHardware) o;
        return Objects.equals(getDevice(), hardware.getDevice()) &&
                Objects.equals(getElement(), hardware.getElement()) &&
                getType() == hardware.getType() &&
                Objects.equals(getId(), hardware.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDevice(), getElement(), getType());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device", this.getDevice().getId())
                .add("element", this.getElement().getId())
                .add("type", this.getType().toString())
                .build();
    }


    public enum HardwareType {

        PROCESSOR, MAINBOARD, RAM, COOLER, GRAPHIC_CARD, DISK, POWER_PACK, CASE
    }


}
