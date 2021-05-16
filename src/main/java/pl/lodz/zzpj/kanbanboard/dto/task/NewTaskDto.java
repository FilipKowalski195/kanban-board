package pl.lodz.zzpj.kanbanboard.dto.task;

import lombok.Value;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Value
public class NewTaskDto {

    UUID projectUuid;

    @Email
    String creatorEmail;

    @NotBlank
    String name;

    String description;

    @NotNull
    Instant deadLine;

    @NotNull
    TaskDetails.Difficulty difficulty;
}
