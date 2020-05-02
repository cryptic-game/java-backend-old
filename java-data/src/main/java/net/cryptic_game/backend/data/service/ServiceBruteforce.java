package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;


/**
 * Entity representing a bruteforce service entry in the database
 *
 * @since 0.3.0
 */
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

    /**
     * Returns the time when the {@link ServiceBruteforce} started the attack
     *
     * @return the time started
     */
    public int getStarted() {
        return this.started;
    }

    /**
     * Sets a new time when the {@link ServiceBruteforce} started attacking
     *
     * @param started the new time to be set
     */
    public void setStarted(final int started) {
        this.started = started;
    }

    /**
     * Returns the {@link Service} which the {@link ServiceBruteforce} attacks
     *
     * @return the targeted {@link Service}
     */
    public Service getTargetService() {
        return this.targetService;
    }

    /**
     * Sets the {@link Service} which the {@link ServiceBruteforce attacks}
     *
     * @param targetService the new target-{@link Service}
     */
    public void setTargetService(final Service targetService) {
        this.targetService = targetService;
    }

    /**
     * Returns the progress of the attack
     *
     * @return the progress
     */
    public float getProgress() {
        return this.progress;
    }

    /**
     * Sets the progress of the attack
     *
     * @param progress the new progress
     */
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ServiceBruteforce} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("started", this.getStarted())
                .add("targetService", this.getTargetService().getId())
                .add("progress", this.getProgress())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link ServiceBruteforce}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link ServiceBruteforce} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceBruteforce that = (ServiceBruteforce) o;
        return getStarted() == that.getStarted() &&
                Float.compare(that.getProgress(), getProgress()) == 0 &&
                Objects.equals(getTargetService(), that.getTargetService());
    }

    /**
     * Hashes the {@link ServiceBruteforce} using {@link Objects} hash method
     *
     * @return Hash of the {@link ServiceBruteforce}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getStarted(), getTargetService(), getProgress());
    }
}
