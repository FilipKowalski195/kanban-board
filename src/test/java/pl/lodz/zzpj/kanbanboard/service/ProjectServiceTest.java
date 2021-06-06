package pl.lodz.zzpj.kanbanboard.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectsRepository projectsRepository;

    @Mock
    UsersRepository usersRepository;

    @Mock
    DateProvider dateProvider;

    ProjectService projectService;

    final String defaultEmail = "user@email.com";
    final String leaderEmail = "leader@email.com";

    final User defaultLeader = new User(UUID.randomUUID(), Instant.now(), leaderEmail, "Damian", "Baczyński", "P@ssw0rd1");
    final User defaultUser = new User(UUID.randomUUID(), Instant.now(), defaultEmail, "Filip", "Kowalski", "P@ssw0rd2");

    final UUID projectUuid = UUID.randomUUID();
    final Project defaultProject = new Project(projectUuid, "Default Project", Instant.now(), defaultLeader);

    final String userNotExistEmail = "userNotExist@email.com";
    final Instant defaultDate = Instant.ofEpochMilli(0);

    private void noMoreInteractions() {
        verifyNoMoreInteractions(usersRepository, projectsRepository, dateProvider);
    }

    private void prepareProjectService() {
        projectService = new ProjectService(projectsRepository, usersRepository, dateProvider);
    }

    @Test
    void getAll() {
        prepareProjectService();

        projectService.getAll();

        verify(projectsRepository).findAll();

        noMoreInteractions();
    }

    @Test
    void getByUuid() {

        when(projectsRepository.findProjectByUuid(projectUuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        projectService.getByUuid(projectUuid);

        verify(projectsRepository).findProjectByUuid(projectUuid);
    }

    @Test
    void add_leaderDoesNotExist_ExceptionThrown() {

        when((usersRepository.findUserByEmail(userNotExistEmail)))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(() -> projectService.add("New Project", userNotExistEmail))
                .isInstanceOf(NotFoundException.class);

        verify(usersRepository)
                .findUserByEmail(userNotExistEmail);

        noMoreInteractions();

    }

    @Test
    void add_leaderDoesExist_ProjectAdded() throws BaseException {
        when((usersRepository.findUserByEmail(defaultEmail)))
                .thenReturn(Optional.of(defaultUser));

        when(dateProvider.now())
                .thenReturn(defaultDate);

        prepareProjectService();

        projectService.add("New project", defaultEmail);

        ArgumentMatcher<Project> projectMatcher =
                (project) -> project.getLeader() == defaultUser &&
                        project.getName().equals("New project") &&
                        project.getMembers().contains(defaultUser) &&
                        project.getMembers().size() == 1;

        verify(usersRepository).findUserByEmail(defaultEmail);
        verify(dateProvider).now();
        verify(projectsRepository).save(ArgumentMatchers.argThat(projectMatcher));

        noMoreInteractions();

    }

    @Test
    void addMember_projectDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(() -> projectService.addMember(uuid, defaultEmail))
                .isInstanceOf(NotFoundException.class);

        verify(projectsRepository).findProjectByUuid(uuid);

        noMoreInteractions();

    }

    @Test
    void addMember_userDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.empty());

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        assertThatThrownBy(() -> projectService.addMember(uuid, defaultEmail))
                .isInstanceOf(NotFoundException.class);

        verify(usersRepository).findUserByEmail(defaultEmail);

        noMoreInteractions();
    }

    @Test
    void addMember_memberAdded() throws BaseException {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.of(defaultUser));

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        projectService.addMember(uuid, defaultEmail);

        verify(projectsRepository).findProjectByUuid(uuid);
        verify(usersRepository).findUserByEmail(defaultEmail);
        verify(projectsRepository).save(defaultProject);

        assertThat(defaultProject.getMembers()).isNotEmpty();

        noMoreInteractions();
    }

    @Test
    void removeMember_projectDoesNotExist_ExceptionThrown() {
        UUID uuid = UUID.randomUUID();

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(() -> projectService.removeMember(uuid, defaultEmail))
                .isInstanceOf(NotFoundException.class);

        verify(projectsRepository).findProjectByUuid(uuid);

        noMoreInteractions();

    }

    @Test
    void removeMember_userDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.empty());

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        assertThatThrownBy(() -> projectService.removeMember(uuid, defaultEmail))
                .isInstanceOf(NotFoundException.class);

        verify(usersRepository).findUserByEmail(defaultEmail);

        noMoreInteractions();
    }

    @Test
    void removeMember_memberRemoved() throws BaseException {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.of(defaultUser));

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        defaultProject.addMember(defaultUser);

        prepareProjectService();

        assertThat(defaultProject.getMembers()).hasSize(1);

        projectService.removeMember(uuid, defaultEmail);

        verify(projectsRepository).findProjectByUuid(uuid);
        verify(usersRepository).findUserByEmail(defaultEmail);

        verify(projectsRepository).save(defaultProject);

        assertThat(defaultProject.getMembers()).isEmpty();


        noMoreInteractions();
    }

    @Test
    void addTask_projectDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(
                () -> projectService.addTask(uuid, defaultEmail, "Test Task", "Test Description", Instant.now(), Difficulty.HIGH))
                .isInstanceOf(NotFoundException.class);

        verify(projectsRepository).findProjectByUuid(uuid);

        noMoreInteractions();
    }

    @Test
    void addTask_creatorDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(userNotExistEmail))
                .thenReturn(Optional.empty());

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        assertThatThrownBy(
                () -> projectService.addTask(uuid, userNotExistEmail, "Test Task", "Test Description", Instant.now(), Difficulty.HIGH))
                .isInstanceOf(NotFoundException.class);

        verify(usersRepository).findUserByEmail(userNotExistEmail);

        noMoreInteractions();
    }

    @Test
    void addTask_creatorIsNotProjectMember_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.of(defaultUser));

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        when(projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultUser))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(
                () -> projectService.addTask(uuid, defaultEmail, "Test Task", "Test Description", Instant.now(), Difficulty.HIGH))
                .isInstanceOf(ConflictException.class);

        verify(usersRepository).findUserByEmail(defaultEmail);
        verify(projectsRepository).findProjectByUuid(uuid);

        noMoreInteractions();
    }

    @Test
    void addTask_TaskAdded() throws BaseException {

        UUID uuid = UUID.randomUUID();

        when(usersRepository.findUserByEmail(leaderEmail))
                .thenReturn(Optional.of(defaultLeader));

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        when(projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultLeader))
                .thenReturn(Optional.of(defaultProject));

        when(dateProvider.now())
                .thenReturn(Instant.now());

        prepareProjectService();

        projectService.addTask(uuid, leaderEmail, "Test Task", "Test Description", Instant.now(), Difficulty.HIGH);

        verify(usersRepository).findUserByEmail(leaderEmail);
        verify(projectsRepository).findProjectByUuid(uuid);
        verify(projectsRepository).findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultLeader);
        verify(dateProvider).now();
        verify(projectsRepository).save(defaultProject);

        assertThat(defaultProject.getTasks()).isNotEmpty();
        noMoreInteractions();

    }

    @Test
    void changeName_projectDoesNotExist_ExceptionThrown() {

        UUID uuid = UUID.randomUUID();

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.empty());

        prepareProjectService();

        assertThatThrownBy(() -> projectService.changeName(uuid, "New New Name"))
                .isInstanceOf(NotFoundException.class);

        verify(projectsRepository).findProjectByUuid(uuid);

        noMoreInteractions();

    }

    @Test
    void changeName_nameChanged() throws BaseException {

        UUID uuid = UUID.randomUUID();

        when(projectsRepository.findProjectByUuid(uuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        projectService.changeName(uuid, "New New Name");

        verify(projectsRepository).findProjectByUuid(uuid);

        verify(projectsRepository).save(defaultProject);

        assertThat(defaultProject)
                .extracting(Project::getName)
                .isEqualTo("New New Name");

        noMoreInteractions();

    }
}