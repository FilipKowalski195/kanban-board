package pl.lodz.zzpj.kanbanboard.dto.task;

import lombok.Value;
import pl.lodz.zzpj.kanbanboard.dto.review.ReviewDto;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
public class TaskDto {

    UUID uuid;

    Instant createdAt;

    String creator;

    Status status;

    String assignee;

    Instant closedAt;

    String name;

    String description;

    Instant deadLine;

    Difficulty difficulty;

    List<ReviewDto> reviews;
}
