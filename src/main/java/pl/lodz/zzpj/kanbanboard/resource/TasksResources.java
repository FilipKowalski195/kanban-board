package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.review.UpdateReviewCommentDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDetailsDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDto;
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

    @PutMapping("/task/{taskUuid}/close")
    public void close(@PathVariable UUID taskUuid) throws BaseException {
        taskService.close(taskUuid);
    }

    @PutMapping("/task/details")
    public void updateTaskDetails(@RequestBody @Valid TaskDetailsDto newTaskDetails)
            throws BaseException {
        taskService.updateTaskDetails(
                newTaskDetails.getTaskUuid(),
                newTaskDetails.getName(),
                newTaskDetails.getDescription(),
                newTaskDetails.getDeadLine(),
                newTaskDetails.getDifficulty()
        );
    }

    @PutMapping("/task/{taskUuid}/status/{status}")
    public void changeStatus(@PathVariable UUID taskUuid, @PathVariable Status status)
            throws BaseException {
        taskService.changeStatus(taskUuid, status);
    }

    @PutMapping("/task/review/comment")
    public void updateReviewComment(@RequestBody @Valid UpdateReviewCommentDto newReviewComment)
            throws NotFoundException {
        taskService.updateReviewComment(
                newReviewComment.getReviewUuid(),
                newReviewComment.getComment()
        );

    }
}
