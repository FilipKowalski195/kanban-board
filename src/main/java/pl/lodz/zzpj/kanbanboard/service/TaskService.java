package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.Review;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ReviewsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TaskDetailsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TasksRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.*;

@Service
public class TaskService extends BaseService {

    private final TasksRepository tasksRepository;

    private final TaskDetailsRepository taskDetailsRepository;

    private final ReviewsRepository reviewsRepository;

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    @Autowired
    public TaskService(
            TasksRepository tasksRepository, TaskDetailsRepository taskDetailsRepository,
            ReviewsRepository reviewsRepository, UsersRepository usersRepository, DateProvider dateProvider) {
        this.tasksRepository = tasksRepository;
        this.taskDetailsRepository = taskDetailsRepository;
        this.reviewsRepository = reviewsRepository;
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
    }

    public Optional<Task> getTaskByUUID(UUID taskUuid) {
        return tasksRepository.findByUuid(taskUuid);
    }

    public List<Task> getAllTasksCreatedBy(String userEmail) {
        return tasksRepository.findAllByCreator_Email(userEmail);
    }

    public List<Task> getAllTaskAssignedTo(String userEmail) {
        return tasksRepository.findAllByAssignee_Email(userEmail);
    }

    // TODO
    // Maybe we should add some generic filters for Tasks
    // They may be useful at implementation of extended logic

    public void close(UUID taskUuid) throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        // Cannot close task if it is not DONE or CANCELED
        if (Set.of(Status.TODO, Status.IN_PROGRESS, Status.TO_REVIEW).contains(task.getStatus())) {
            throw ConflictException.cannotCloseTask(task);
        }
        task.setClosedAt(dateProvider.now());
        tasksRepository.save(task);
    }

    public void updateTaskDetails(
            UUID taskUuid, String name, String description, Instant deadLine, Difficulty difficulty)
            throws BaseException {
        var taskDetails = getTaskByUuidOrThrow(taskUuid).getDetails();

        taskDetails.setName(name);
        taskDetails.setDescription(description);
        taskDetails.setDeadLine(deadLine);
        taskDetails.setDifficulty(difficulty);
        catchingValidation(() -> taskDetailsRepository.save(taskDetails));
    }

    public void changeStatus(UUID taskUuid, Status newStatus) throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        var taskReviews = task.getDetails().getReviews();
        var lastReview = taskReviews.isEmpty() ? null : taskReviews.get(taskReviews.size() - 1);

        // Cannot change task's status to TO_REVIEW or DONE if it's TO DO
        if (Status.TODO.equals(task.getStatus()) && Set.of(Status.TO_REVIEW, Status.DONE).contains(newStatus)) {
            throw ConflictException.invalidTaskStatus(task, newStatus);
        }

        // Cannot change task's status to DONE if it isn't positively reviewed
        if (Status.DONE.equals(newStatus)) {

            if (lastReview == null) {
                throw ConflictException.noReview(task);
            }

            if (lastReview.isRejected()) {
                throw ConflictException.unsatisfiedReview(task, lastReview);
            }
        }

        task.setStatus(newStatus);
        tasksRepository.save(task);
    }

    public void updateReviewComment(UUID reviewUuid, String newComment) throws NotFoundException {
        var review = getReviewByUuidOrThrow(reviewUuid);

        review.setComment(newComment);
        reviewsRepository.save(review);
    }

    private Task getTaskByUuidOrThrow(UUID taskUuid) throws NotFoundException {
        return tasksRepository.findByUuid(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));
    }

    private Review getReviewByUuidOrThrow(UUID reviewUuid) throws NotFoundException {
        return reviewsRepository.findByUuid(reviewUuid)
                .orElseThrow(() -> NotFoundException.notFound(Review.class, "uuid", reviewUuid));
    }

    private User getUserByEmailOrThrow(String email) throws NotFoundException {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }
}
