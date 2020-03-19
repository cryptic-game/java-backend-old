package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

public class Service extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @JoinColumn(name = "name", updatable = true, nullable = true)
    private String name;

    @JoinColumn(name = "running", updatable = true, nullable = true, columnDefinition = "TINYINT")
    private boolean running;

    @JoinColumn(name = "running_port", updatable = true, nullable = true)
    private int runningPort;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getRunningPort() {
        return runningPort;
    }

    public void setRunningPort(int runningPort) {
        this.runningPort = runningPort;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("name", this.getName())
                .add("isRunning", this.isRunning())
                .add("runningPort", this.getRunningPort())
                .add("user", this.getUser().getId())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return isRunning() == service.isRunning() &&
                getRunningPort() == service.getRunningPort() &&
                Objects.equals(getDevice(), service.getDevice()) &&
                Objects.equals(getName(), service.getName()) &&
                Objects.equals(getUser(), service.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getName(), isRunning(), getRunningPort(), getUser());
    }
}
