package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TaskDetails {

    // LOW ~1h, MEDIUM ~4h, HIGH ~8h
    enum Difficulty {
        LOW, MEDIUM, HIGH
    }

    private String name;

    private String description;

    private Instant deadLine;

    private Difficulty difficulty;

    private final List<Review> reviews;
}
