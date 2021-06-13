package pl.lodz.zzpj.kanbanboard.service;

import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.*;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.*;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.*;

@Service
public class TaskService extends BaseService {

    private final TasksRepository tasksRepository;

    private final ProjectsRepository projectsRepository;

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    @Autowired
    public TaskService(
            TasksRepository tasksRepository,
            ProjectsRepository projectsRepository, UsersRepository usersRepository,
            DateProvider dateProvider
    ) {
        this.tasksRepository = tasksRepository;
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
    }

    public Task getTaskByUUID(UUID taskUuid) throws NotFoundException {
        return getTaskByUuidOrThrow(taskUuid);
    }

    public List<Task> getAllTasksCreatedBy(String userEmail) {
        return tasksRepository.findAllByCreator_Email(userEmail);
    }

    public List<Task> getAllTaskAssignedTo(String userEmail) {
        return tasksRepository.findAllByAssignee_Email(userEmail);
    }

    public void updateTaskDetails(
            UUID taskUuid,
            String name,
            String description,
            Instant deadLine,
            Difficulty difficulty
    ) throws BaseException {
        var task  = getTaskByUuidOrThrow(taskUuid);
        var taskDetails = task.getDetails();

        taskDetails.setName(name);
        taskDetails.setDescription(description);
        taskDetails.setDeadLine(deadLine);
        taskDetails.setDifficulty(difficulty);

        task.setDetails(taskDetails);

        catchingValidation(() -> tasksRepository.save(task));
    }

    public void changeStatus(UUID taskUuid, Status newStatus) throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        if (newStatus == Status.CANCELED) {
            task.cancelTask(dateProvider.now());
            catchingValidation(() -> tasksRepository.save(task));
            return;
        }

        if (!task.isAssigned()) {
            throw ConflictException.taskNotAssigned(task);
        }

        if (task.isFinished()) {
            throw ConflictException.closedTask();
        }

        if (newStatus == Status.DONE) {
            assertReview(task);
            task.closeTask(dateProvider.now());
        } else {
            task.setStatus(newStatus);
        }

        catchingValidation(() -> tasksRepository.save(task));
    }

    private void assertReview(Task task) throws ConflictException {
        var reviews = task.getDetails().getReviews();
        var lastReview = Iterables.getLast(reviews, null);
        var status = task.getStatus();

        if (status != Status.TO_REVIEW || lastReview == null) {
            throw ConflictException.noReview(task);
        }

        if (lastReview.isRejected()) {
            throw ConflictException.rejectingReview(task, lastReview);
        }
    }

    public void assign(UUID taskUuid, String assigneeEmail) throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        if (task.isFinished()) {
            throw ConflictException.closedTask();
        }

        var assignee = getUserByEmailOrThrow(assigneeEmail);
        var project = projectsRepository
                .findProjectByTasksContains(task)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "task", taskUuid));

        assertUserBeingProjectMember(project, assignee);

        task.setAssignee(assignee);
        tasksRepository.save(task);
    }

    public void reviewTask(UUID taskUuid, String reviewerEmail, String comment, boolean rejected) throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        if (!Status.TO_REVIEW.equals(task.getStatus())) {
            throw ConflictException.taskNotToReview(task);
        }

        var reviewer = getUserByEmailOrThrow(reviewerEmail);
        var project = projectsRepository.findProjectByTasksContains(task)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "task", task));

        assertUserBeingProjectMember(project, reviewer);

        var newReview = new Review(UUID.randomUUID(), dateProvider.now(), reviewer, comment, rejected);

        var taskDetails = task.getDetails();

        taskDetails.getReviews().add(newReview);
        task.setDetails(taskDetails);
        task.setStatus(Status.IN_PROGRESS);

        catchingValidation(() -> tasksRepository.save(task));
    }

    private void assertUserBeingProjectMember(Project project, User user) throws ConflictException {

        var findProjectWithMember = projectsRepository
                .findProjectByUuidAndMembersContains(project.getUuid(), user);

        if (findProjectWithMember.isEmpty()) {
            throw ConflictException.notMemberOfProject(user, project);
        }
    }

    private Task getTaskByUuidOrThrow(UUID taskUuid) throws NotFoundException {
        return tasksRepository.findByUuid(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));
    }

    private User getUserByEmailOrThrow(String email) throws NotFoundException {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }
}
