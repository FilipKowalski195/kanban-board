package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public abstract class Base {

    @EqualsAndHashCode.Include
    private final UUID uuid;

    private final Instant createdAt;
}
