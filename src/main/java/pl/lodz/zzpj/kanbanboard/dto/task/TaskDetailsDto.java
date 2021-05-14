package pl.lodz.zzpj.kanbanboard.dto.task;

import lombok.Value;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Value
public class TaskDetailsDto {

    UUID taskUuid;

    @NotBlank
    String name;

    String description;

    @FutureOrPresent
    @NotNull
    Instant deadLine;

    @NotNull
    Difficulty difficulty;
}
