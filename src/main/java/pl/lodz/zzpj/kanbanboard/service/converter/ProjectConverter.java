package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.dto.project.ProjectDto;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectConverter {

    private ProjectConverter() {

    }

    public static ProjectDto toDto(Project project) {

        var memberMails = project
                .getMembers()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
        var taskUuids = project
                .getTasks()
                .stream()
                .map(Task::getUuid)
                .map(UUID::toString)
                .collect(Collectors.toSet());

        return new ProjectDto(
                project.getUuid(),
                project.getCreatedAt(),
                project.getName(),
                project.getLeader().getEmail(),
                memberMails,
                taskUuids
        );
    }
}
