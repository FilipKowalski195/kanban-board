package pl.lodz.zzpj.kanbanboard.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectsRepository projectRepository;

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

    final String userNotExistEmail =  "userNotExist@email.com";
    final Instant defaultDate = Instant.ofEpochMilli(0);

    private void noMoreInteractions() {
        verifyNoMoreInteractions(usersRepository, projectRepository, dateProvider);
    }

    private void prepareProjectService() {
        projectService = new ProjectService(projectRepository, usersRepository, dateProvider);
    }

    @Test
    void getAll() {
        prepareProjectService();

        projectService.getAll();

        verify(projectRepository).findAll();

        noMoreInteractions();
    }

    @Test
    void getByUuid() {

        when(projectRepository.findProjectByUuid(projectUuid))
                .thenReturn(Optional.of(defaultProject));

        prepareProjectService();

        projectService.getByUuid(projectUuid);

        verify(projectRepository).findProjectByUuid(projectUuid);
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
        verify(projectRepository).save(ArgumentMatchers.argThat(projectMatcher));

        noMoreInteractions();

    }

}