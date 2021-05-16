package pl.lodz.zzpj.kanbanboard.dto.project;

import lombok.Value;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Value
public class ProjectDto {

    UUID uuid;

    Instant createdAt;

    String name;

    String leaderEmail;

    Set<String> members;

    Set<String> tasks;

}
