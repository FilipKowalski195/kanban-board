package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.review.NewReviewDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDetailsDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDto;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.TaskService;
import pl.lodz.zzpj.kanbanboard.service.converter.TaskConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class TasksResources {

    private final TaskService taskService;

    @Autowired
    public TasksResources(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/task/{taskUuid}")
    public Task getTaskByUuid(@PathVariable UUID taskUuid) throws NotFoundException {
        return taskService.getTaskByUUID(taskUuid);
    }

    @GetMapping("/task/createdBy/{email}")
    public List<TaskDto> getAllTasksCreatedBy(@PathVariable String email) {
        return taskService.getAllTasksCreatedBy(email)
                .stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/task/assignedTo/{email}")
    public List<TaskDto> getAllTasksAssignedTo(@PathVariable String email) {
        return taskService.getAllTaskAssignedTo(email)
                .stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/task/{taskUuid}/assignee")
    public void assignee(@PathVariable UUID taskUuid, @RequestParam String email) throws BaseException {
        taskService.assign(taskUuid, email);
    }

    @PostMapping("/task/{taskUuid}/review")
    public void reviewTask(@PathVariable UUID taskUuid, @RequestBody NewReviewDto newReview) throws BaseException {
        taskService.reviewTask(
                taskUuid,
                newReview.getReviewerEmail(),
                newReview.getComment(),
                newReview.getRejected()
        );
    }

    @PutMapping("/task/{taskUuid}/details")
    public void updateTaskDetails(
            @PathVariable UUID taskUuid,
            @RequestBody @Valid TaskDetailsDto newTaskDetails
    ) throws BaseException {

        taskService.updateTaskDetails(
                taskUuid,
                newTaskDetails.getName(),
                newTaskDetails.getDescription(),
                newTaskDetails.getDeadLine(),
                newTaskDetails.getDifficulty()
        );
    }

    @PutMapping("/task/{taskUuid}/status")
    public void changeStatus(@PathVariable UUID taskUuid, @RequestParam Status status) throws BaseException {
        taskService.changeStatus(taskUuid, status);
    }
}
