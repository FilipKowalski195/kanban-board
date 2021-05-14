package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.Review;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ReviewsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TaskDetailsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TasksRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<Task> getAll() {
        return tasksRepository.findAll();
    }

    public List<Task> getAllTasksCreatedBy(String userEmail) {
        return getAll()
                .stream()
                .filter(task -> task.getCreator().getEmail().equals(userEmail))
                .collect(Collectors.toList());
    }

    public List<Task> getAllTaskAssignedTo(String userEmail) {
        return getAll()
                .stream()
                .filter(task -> task.getAssignee().getEmail().equals(userEmail))
                .collect(Collectors.toList());
    }

    // TODO
    // Maybe we should add some generic filters for Tasks
    // They may be useful at implementation of extended logic

    public void add(String creatorEmail, String name, String description, Instant deadLine, Difficulty difficulty)
            throws NotFoundException, BadOperationException {
        var creator = usersRepository.findUserByEmail(creatorEmail)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", creatorEmail));

        var newTaskDetails = new TaskDetails(name, description, deadLine, difficulty, new ArrayList<>());

        catchingValidation(() -> taskDetailsRepository.save(newTaskDetails));

        var newTask = new Task(UUID.randomUUID(), dateProvider.now(), creator, newTaskDetails);

        catchingValidation(() -> tasksRepository.save(newTask));
    }

    public void assign(UUID taskUuid, String assigneeEmail) throws NotFoundException {
        var assignee = usersRepository.findUserByEmail(assigneeEmail)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", assigneeEmail));

        var task = tasksRepository.findByUuid(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));

        task.setAssignee(assignee);
        tasksRepository.save(task);
    }

    public void close(UUID taskUuid) throws NotFoundException, ConflictException {
        var task = getTaskByUUID(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));

        // Cannot close task if it is not DONE or CANCELED
        if(Set.of(Status.TODO, Status.IN_PROGRESS, Status.TO_REVIEW).contains(task.getStatus())){
            throw ConflictException.invalidTaskStatus(task);
        }
        task.setClosedAt(dateProvider.now());
        tasksRepository.save(task);
    }

    public void updateTaskDetails(
            UUID taskUuid, String name, String description, Instant deadLine, Difficulty difficulty)
            throws NotFoundException, BadOperationException {
        var taskDetails = getTaskByUUID(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid))
                .getDetails();

        taskDetails.setName(name);
        taskDetails.setDescription(description);
        taskDetails.setDeadLine(deadLine);
        taskDetails.setDifficulty(difficulty);
        catchingValidation(() -> taskDetailsRepository.save(taskDetails));
    }

    public void changeStatus(UUID taskUuid, Status newStatus) throws NotFoundException, ConflictException {
        var task = getTaskByUUID(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));

        var taskReviews = task.getDetails().getReviews();
        var lastReview = taskReviews.isEmpty()? null : taskReviews.get(taskReviews.size() -1);

        // Cannot change task's status to DONE if it isn't positively reviewed
        if((lastReview == null || lastReview.isRejected()) && Status.DONE.equals(newStatus)){
            if(Set.of(Status.TODO, Status.IN_PROGRESS, Status.TO_REVIEW).contains(task.getStatus())){
                throw ConflictException.unsatisfiedReview(task, lastReview);
            }
        }

        task.setStatus(newStatus);
        tasksRepository.save(task);
    }

    public void addReview(UUID taskUuid, String reviewerEmail, String comment, boolean rejected)
            throws BadOperationException, NotFoundException, ConflictException {
        var task = getTaskByUUID(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));

        // Cannot add review to task that isn't TO_REVIEW
        if(!Status.TO_REVIEW.equals(task.getStatus())){
            throw ConflictException.cannotAddReview(task);
        }

        var reviewer = usersRepository.findUserByEmail(reviewerEmail)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", reviewerEmail));

        var newReview = new Review(UUID.randomUUID(), dateProvider.now(), reviewer, comment, rejected);

        catchingValidation(() -> reviewsRepository.save(newReview));

        var taskDetails = task.getDetails();

        taskDetails.getReviews().add(newReview);
        taskDetailsRepository.save(taskDetails);
    }

    public void updateReviewComment(UUID reviewUuid, String newComment) throws NotFoundException {
        var review = reviewsRepository.findByUuid(reviewUuid)
                .orElseThrow(() -> NotFoundException.notFound(Review.class, "uuid", reviewUuid));

        review.setComment(newComment);
        reviewsRepository.save(review);
    }
}
