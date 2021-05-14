package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.review.NewReviewDto;
import pl.lodz.zzpj.kanbanboard.dto.review.UpdateReviewCommentDto;
import pl.lodz.zzpj.kanbanboard.dto.task.NewTaskDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDetailsDto;
import pl.lodz.zzpj.kanbanboard.dto.task.TaskDto;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
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

    @GetMapping("/task")
    public List<TaskDto> getAll() {
        return taskService.getAll()
                .stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
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

    @PostMapping("/task")
    public void addTask(@RequestBody @Valid NewTaskDto newTask) throws NotFoundException, BadOperationException {
        taskService.add(newTask.getCreatorEmail(), newTask.getName(), newTask.getDescription(), newTask.getDeadLine(),
                newTask.getDifficulty());
    }

    @PutMapping("/task/{taskUuid}/assign/{email}")
    public void assign(@PathVariable UUID taskUuid, @PathVariable String email) throws NotFoundException {
        taskService.assign(taskUuid, email);
    }

    @PutMapping("/task/{taskUuid}/close")
    public void close(@PathVariable UUID taskUuid) throws NotFoundException, ConflictException {
        taskService.close(taskUuid);
    }

    @PutMapping("/task/details")
    public void updateTaskDetails(@RequestBody @Valid TaskDetailsDto newTaskDetails)
            throws NotFoundException, BadOperationException {
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
            throws NotFoundException, ConflictException {
        taskService.changeStatus(taskUuid, status);
    }

    @PostMapping("/task/review")
    public void addReview(@RequestBody @Valid NewReviewDto newReview)
            throws NotFoundException, BadOperationException, ConflictException {
        taskService.addReview(
                newReview.getTaskUuid(),
                newReview.getReviewerEmail(),
                newReview.getComment(),
                newReview.isRejected()
        );
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
