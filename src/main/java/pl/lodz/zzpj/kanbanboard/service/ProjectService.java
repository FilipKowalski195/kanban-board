package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
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

    private final DateProvider dateProvider;

    @Autowired
    public ProjectService(
            ProjectsRepository projectsRepository,
            UsersRepository usersRepository,
            DateProvider dateProvider
    ) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
    }

    public List<Project> getAll(){
        return projectsRepository.findAll();
    }

    public Optional<Project> getByUuid(UUID projectUuid){
        return projectsRepository.findProjectByUuid(projectUuid);
    }

    public void add(String name, String leaderEmail) throws BaseException {
        var leader = getUserByEmailOrThrow(leaderEmail);

        var project = new Project(UUID.randomUUID(), name, dateProvider.now(), leader);

        project.addMember(leader);
        catchingValidation(() -> projectsRepository.save(project));
    }

    public void addMember(UUID projectUuid, String newMemberEmail) throws BaseException {
        var project = getProjectByUuidOrThrow(projectUuid);

        var newMember = getUserByEmailOrThrow(newMemberEmail);

        project.addMember(newMember);
        catchingValidation(() -> projectsRepository.save(project));
    }

    public void removeMember(UUID projectUuid, String memberEmail) throws NotFoundException {
        var project = getProjectByUuidOrThrow(projectUuid);
        var member = getUserByEmailOrThrow(memberEmail);

        project.removeMember(member.getUuid());

        projectsRepository.save(project);
    }

    public void addTask(
            UUID projectUuid,
            String creatorEmail,
            String name,
            String description,
            Instant deadLine,
            Difficulty difficulty
    ) throws BaseException {
        var project = getProjectByUuidOrThrow(projectUuid);
        var creator = getUserByEmailOrThrow(creatorEmail);

        assertUserBeingProjectMember(project, creator);

        var newTaskDetails = new TaskDetails(name, description, deadLine, difficulty, new ArrayList<>());
        var newTask = new Task(UUID.randomUUID(), dateProvider.now(), creator, newTaskDetails);

        project.addTask(newTask);
        catchingValidation(() -> projectsRepository.save(project));
    }

    public void changeName(UUID projectUuid, String name) throws BaseException {
        var project = getProjectByUuidOrThrow(projectUuid);
        project.setName(name);

        catchingValidation(() -> projectsRepository.save(project));
    }

    private void assertUserBeingProjectMember(Project project, User user) throws ConflictException {

        var findProjectWithMember = projectsRepository
                .findProjectByUuidAndMembersContains(project.getUuid(), user);

        if (findProjectWithMember.isEmpty()) {
            throw ConflictException.notMemberOfProject(user, project);
        }
    }

    private User getUserByEmailOrThrow(String email) throws NotFoundException {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }

    private Project getProjectByUuidOrThrow(UUID projectUuid) throws NotFoundException {
        return projectsRepository.findProjectByUuid(projectUuid)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "uuid", projectUuid));
    }
}
