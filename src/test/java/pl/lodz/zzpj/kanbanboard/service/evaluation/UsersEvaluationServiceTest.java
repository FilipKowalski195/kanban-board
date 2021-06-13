package pl.lodz.zzpj.kanbanboard.service.evaluation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TasksRepository;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.EvaluatorsFactory;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UserEvaluation;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator.Metric;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersEvaluationServiceTest {

    @Mock
    EvaluatorsFactory evaluatorsFactory;

    @Mock
    ProjectsRepository projectsRepository;

    @Mock
    TasksRepository tasksRepository;

    UsersEvaluationService usersEvaluationService;

    UUID projectUuid = UUID.randomUUID();

    Metric metric = Metric.LAST_PERFORMANCE;

    List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
                    "User@email.com",
                    "Filip",
                    "Kowalski",
                    "PASSW)RD123"
            )
    );

    Project project = new Project(projectUuid, "Test project", Instant.now(), users.get(0));

    Instant getDate(int dayOfMonth) {
        return LocalDate.of(2021, 6, 1 + dayOfMonth).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    TaskDetails taskDetailsEasy = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC), Difficulty.LOW);

    Task task1 = new Task(
            UUID.randomUUID(), getDate(0), users.get(0), Status.DONE, getDate(3), taskDetailsEasy);
    Task task2 = new Task(
            UUID.randomUUID(), getDate(0), users.get(0), Status.DONE, getDate(1), taskDetailsEasy);

    List<Task> tasks = List.of(task2, task1);

    @Test
    void evaluateProjectMembers() throws BaseException {

        when(projectsRepository.findProjectByUuid(projectUuid)).thenReturn(Optional.of(project));

        var evaluator = Mockito.mock(UsersEvaluator.class);
        when(evaluatorsFactory.createEvaluator(metric)).thenReturn(evaluator);

        when(tasksRepository.findAllByStatusEquals(Status.DONE)).thenReturn(tasks);

        var evals = List.of(new UserEvaluation(users.get(0), 6.0));
        when(evaluator.evaluate(project.getMembers(), tasks)).thenReturn(evals);

        init();

        var actual = usersEvaluationService.evaluateProjectMembers(projectUuid, metric);

        verify(projectsRepository).findProjectByUuid(projectUuid);

        verify(evaluatorsFactory).createEvaluator(metric);

        verify(tasksRepository).findAllByStatusEquals(Status.DONE);

        verify(evaluator).evaluate(project.getMembers(), tasks);

        assertThat(actual).isEqualTo(evals);

    }

    @Test
    void evaluateProjectMembers_ProjectDoesNotExist() {

        when(projectsRepository.findProjectByUuid(projectUuid)).thenReturn(Optional.empty());

        init();

        assertThatThrownBy(() -> usersEvaluationService.evaluateProjectMembers(projectUuid, metric)).isInstanceOf(
                NotFoundException.class
        );

    }

    private void init() {
        usersEvaluationService = new UsersEvaluationService(evaluatorsFactory, projectsRepository, tasksRepository);
    }
}