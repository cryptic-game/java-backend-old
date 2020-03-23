package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "service_bruteforce")
public class ServiceBruteforce extends TableModelAutoId {

    @Column(name = "started", updatable = false, nullable = true) // updatable?
    private int started;

    @ManyToOne
    @JoinColumn(name = "target_service", updatable = true, nullable = true)
    @Type(type = "uuid-char")
    private Service targetService;

    @Column(name = "progress", updatable = true, nullable = true)
    private float progress;

    public int getStarted() {
        return this.started;
    }

    public void setStarted(final int started) {
        this.started = started;
    }

    public Service getTargetService() {
        return this.targetService;
    }

    public void setTargetService(final Service targetService) {
        this.targetService = targetService;
    }


    public float getProgress() {
        return this.progress;
    }

    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("started", this.getStarted())
                .add("targetService", this.getTargetService().getId())
                .add("progress", this.getProgress())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceBruteforce that = (ServiceBruteforce) o;
        return getStarted() == that.getStarted() &&
                Float.compare(that.getProgress(), getProgress()) == 0 &&
                Objects.equals(getTargetService(), that.getTargetService());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStarted(), getTargetService(), getProgress());
    }
}
