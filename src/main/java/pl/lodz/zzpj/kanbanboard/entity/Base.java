package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class Base {


    @NotNull
    @Column(nullable = false)
    private UUID uuid;

    @PastOrPresent
    @NotNull
    @Column(nullable = false)
    private Instant createdAt;

    protected Base() {
    }

    protected Base(UUID uuid, Instant createdAt) {
        this.uuid = uuid;
        this.createdAt = createdAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
