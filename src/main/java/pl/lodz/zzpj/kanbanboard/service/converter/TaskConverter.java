package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.dto.task.TaskDto;
import pl.lodz.zzpj.kanbanboard.entity.Task;

import java.util.stream.Collectors;

public final class TaskConverter {

    private TaskConverter() {
    }

    public static TaskDto toDto(Task task){
        return new TaskDto(
                task.getUuid(),
                task.getCreatedAt(),
                task.getCreator().getEmail(),
                task.getStatus(),
                task.getAssignee() == null ? null : task.getAssignee().getEmail(),
                task.getClosedAt(),
                task.getDetails().getName(),
                task.getDetails().getDescription(),
                task.getDetails().getDeadLine(),
                task.getDetails().getDifficulty(),
                task.getDetails().getReviews()
                        .stream()
                        .map(ReviewConverter::toDto)
                        .collect(Collectors.toList())
        );
    }
}
