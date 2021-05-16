package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.project.NewProjectDto;
import pl.lodz.zzpj.kanbanboard.dto.review.NewReviewDto;
import pl.lodz.zzpj.kanbanboard.dto.task.NewTaskDto;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.ProjectService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

@RestController
public class ProjectsResources {

    private final ProjectService projectService;

    public ProjectsResources(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/prject")
    public List<Project> getAll(){
        return projectService.getAll();
    }

    @GetMapping("/project/leader/{email}")
    public List<Project> getAllByLeaderEmail(@PathVariable @Email String email){
        return projectService.getAllByLeader(email);
    }

    @GetMapping("/project/member/{email}")
    public List<Project> getAllByMemberEmail(@PathVariable @Email String email){
        return projectService.getAllByMember(email);
    }

    @GetMapping("/project/{projectUuid}")
    public Project getByUuid(@PathVariable @Valid UUID projectUuid) throws NotFoundException {
        return projectService.getByUuid(projectUuid)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "uuid", projectUuid));
    }

    @PostMapping("/project")
    public void add(@RequestBody @Valid NewProjectDto newProject) throws NotFoundException {
        projectService.add(newProject.getName(), newProject.getLeaderEmail());
    }

    @PutMapping("/project/{projectUuid}/add/member/{email}")
    public void addMember(@PathVariable UUID projectUuid, @PathVariable String email) throws NotFoundException {
        projectService.addMember(projectUuid, email);
    }

    @PutMapping("/project/task/{taskUuid}/assign/{email}")
    public void assing(@PathVariable UUID taskUuid, @PathVariable String email)
            throws BaseException {
        projectService.assign(taskUuid, email);
    }

    @PostMapping("/project/review")
    public void addReview(@RequestBody NewReviewDto newReview)
            throws BaseException {
        projectService.addReview(
                newReview.getTaskUuid(),
                newReview.getReviewerEmail(),
                newReview.getComment(),
                newReview.isRejected()
        );
    }

    @PutMapping("/project/{projectUuid}/remove/member/{emial}")
    public void removeMember(@PathVariable UUID projectUuid, @PathVariable String emial) throws NotFoundException {
        projectService.removeMember(projectUuid, emial);
    }

    @PutMapping("/project/{projectUuid}/change/name/{newName}")
    public void changeName(@PathVariable UUID projectUuid, @PathVariable String newName)
            throws BaseException {
        projectService.changeName(projectUuid, newName);
    }

    @PostMapping("/project/task")
    public void addTask(@RequestBody @Valid NewTaskDto newTask)
            throws BaseException {
        projectService.addTask(newTask.getProjectUuid() ,newTask.getCreatorEmail(), newTask.getName(), newTask.getDescription(), newTask.getDeadLine(),
                newTask.getDifficulty());
    }
}
