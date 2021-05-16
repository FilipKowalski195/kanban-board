package pl.lodz.zzpj.kanbanboard.dto.review;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class ReviewDto {

    UUID uuid;

    Instant createdAt;

    String reviewer;

    String comment;

    boolean rejected;
}
