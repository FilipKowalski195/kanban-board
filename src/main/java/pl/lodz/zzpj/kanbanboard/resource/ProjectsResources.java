package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.project.NewProjectDto;
import pl.lodz.zzpj.kanbanboard.dto.project.ProjectDto;
import pl.lodz.zzpj.kanbanboard.dto.task.NewTaskDto;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.ProjectService;
import pl.lodz.zzpj.kanbanboard.service.converter.ProjectConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class ProjectsResources {

    private final ProjectService projectService;

    public ProjectsResources(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/project")
    public List<ProjectDto> getAll(){
        return projectService
                .getAll()
                .stream()
                .map(ProjectConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/project/{projectUuid}")
    public ProjectDto getByUuid(@PathVariable @Valid UUID projectUuid) throws NotFoundException {
        return projectService
                .getByUuid(projectUuid)
                .map(ProjectConverter::toDto)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "uuid", projectUuid));
    }

    @PostMapping("/project")
    public void add(@RequestBody @Valid NewProjectDto newProject) throws BaseException {
        projectService.add(newProject.getName(), newProject.getLeaderEmail());
    }

    @PutMapping("/project/{projectUuid}/member")
    public void addMember(@PathVariable UUID projectUuid, @RequestParam String email) throws BaseException {
        projectService.addMember(projectUuid, email);
    }

    @DeleteMapping("/project/{projectUuid}/member/{email}")
    public void removeMember(@PathVariable UUID projectUuid, @PathVariable String email) throws NotFoundException {
        projectService.removeMember(projectUuid, email);
    }

    @PutMapping("/project/{projectUuid}/name")
    public void changeName(@PathVariable UUID projectUuid, @RequestParam String newName) throws BaseException {
        projectService.changeName(projectUuid, newName);
    }

    @PostMapping("/project/{projectUuid}/task")
    public void addTask(@PathVariable UUID projectUuid, @RequestBody @Valid NewTaskDto newTask) throws BaseException {
        projectService.addTask(
                projectUuid,
                newTask.getCreatorEmail(),
                newTask.getName(),
                newTask.getDescription(),
                newTask.getDeadLine(),
                newTask.getDifficulty()
        );
    }
}
