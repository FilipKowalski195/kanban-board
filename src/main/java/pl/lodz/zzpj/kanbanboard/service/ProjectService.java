package pl.lodz.zzpj.kanbanboard.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService extends BaseService{

    private final ProjectsRepository projectsRepository;

    private final UsersRepository usersRepository;

    private final TasksRepository tasksRepository;

    private final TaskDetailsRepository taskDetailsRepository;

    private final ReviewsRepository reviewsRepository;

    private final DateProvider dateProvider;

    @Autowired
    public ProjectService(
            ProjectsRepository projectsRepository, UsersRepository usersRepository,
            TasksRepository tasksRepository,
            TaskDetailsRepository taskDetailsRepository,
            ReviewsRepository reviewsRepository, DateProvider dateProvider) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.tasksRepository = tasksRepository;
        this.taskDetailsRepository = taskDetailsRepository;
        this.reviewsRepository = reviewsRepository;
        this.dateProvider = dateProvider;
    }

    public List<Project> getAll(){
        return projectsRepository.findAll();
    }

    public List<Project> getAllByLeader(String leaderEmail){
        return projectsRepository.findAllByLeader_Email(leaderEmail);
    }

    public List<Project> getAllByMember(String memberEmail){
        var member = usersRepository.findUserByEmail(memberEmail)
                .orElse(null);

        return projectsRepository.findAllByMembersContains(member);
    }

    public Optional<Project> getByUuid(UUID projectUuid){
        return projectsRepository.findProjectByUuid(projectUuid);
    }

    public void add(String name, String leaderEmail) throws NotFoundException {
        var leader = getUserByEmailOrThrow(leaderEmail);

        var project = new Project(UUID.randomUUID(), name, dateProvider.now(), leader);

        project.addMember(leader);
        projectsRepository.save(project);
    }

    public void addMember(UUID projectUuid, String newMemberEmail) throws NotFoundException {
        var project = getProjectByUuidOrThrow(projectUuid);

        var newMember = getUserByEmailOrThrow(newMemberEmail);

        project.addMember(newMember);
        projectsRepository.save(project);
    }

    public void addTask(UUID proejctUuid ,String creatorEmail, String name, String description, Instant deadLine, Difficulty difficulty)
            throws BaseException {
        var project = getProjectByUuidOrThrow(proejctUuid);

        var creator = getUserByEmailOrThrow(creatorEmail);

        if(!isUserProjectMember(project, creator)){
            throw ConflictException.notMemberOfProject(creator, project);
        }

        var newTaskDetails = new TaskDetails(name, description, deadLine, difficulty, new ArrayList<>());

        catchingValidation(() -> taskDetailsRepository.save(newTaskDetails));

        var newTask = new Task(UUID.randomUUID(), dateProvider.now(), creator, newTaskDetails);

        catchingValidation(() -> tasksRepository.save(newTask));
        project.addTask(newTask);
        catchingValidation(() -> projectsRepository.save(project));
    }

    public void assign(UUID taskUuid, String assigneeEmail) throws BaseException {

        var assignee = getUserByEmailOrThrow(assigneeEmail);

        var task = getTaskByUuidOrThrow(taskUuid);

        var project = projectsRepository.findProjectByTasksContains(task)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "task", task));

        if(!isUserProjectMember(project, assignee)){
            throw ConflictException.notMemberOfProject(assignee, project);
        }

        task.setAssignee(assignee);
        tasksRepository.save(task);
    }

    public void addReview(UUID taskUuid, String reviewerEmail, String comment, boolean rejected)
            throws BaseException {
        var task = getTaskByUuidOrThrow(taskUuid);

        // Cannot add review to task that isn't TO_REVIEW
        if (!Status.TO_REVIEW.equals(task.getStatus())) {
            throw ConflictException.cannotAddReview(task);
        }

        var reviewer = getUserByEmailOrThrow(reviewerEmail);

        var project = projectsRepository.findProjectByTasksContains(task)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "task", task));

        if(!isUserProjectMember(project, reviewer)){
            throw ConflictException.notMemberOfProject(reviewer, project);
        }

        var newReview = new Review(UUID.randomUUID(), dateProvider.now(), reviewer, comment, rejected);

        catchingValidation(() -> reviewsRepository.save(newReview));

        var taskDetails = task.getDetails();

        taskDetails.getReviews().add(newReview);
        taskDetailsRepository.save(taskDetails);
    }

    public void removeMember(UUID projectUuid, String memberEmail) throws NotFoundException {
        var project = getProjectByUuidOrThrow(projectUuid);

        var member = getUserByEmailOrThrow(memberEmail);

        project.removeMember(member.getUuid());
    }

    public void changeName(UUID projectUuid, String name) throws BaseException {
        var project = getProjectByUuidOrThrow(projectUuid);

        project.setName(name);
        catchingValidation(() -> projectsRepository.save(project));
    }

    private User getUserByEmailOrThrow(String email) throws NotFoundException {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }

    private Project getProjectByUuidOrThrow(UUID projectUuid) throws NotFoundException {
        return projectsRepository.findProjectByUuid(projectUuid)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "uuid", projectUuid));
    }

    private Task getTaskByUuidOrThrow(UUID taskUuid) throws NotFoundException {
        return tasksRepository.findByUuid(taskUuid)
                .orElseThrow(() -> NotFoundException.notFound(Task.class, "uuid", taskUuid));
    }

    private boolean isUserProjectMember(Project project,User user){
        return project.getMembers().contains(user);
    }
}
