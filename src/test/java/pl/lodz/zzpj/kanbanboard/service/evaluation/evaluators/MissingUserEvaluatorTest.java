package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissingUserEvaluatorTest {

    @Mock
    UsersEvaluator usersEvaluator;

    MissingUserEvaluator missingUserEvaluator;

    List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
                    "User@email.com",
                    "Filip",
                    "Kowalski",
                    "PASSW)RD123"
            ),
            new User(
                    UUID.randomUUID(),
                    LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
                    "User2@email.com",
                    "Damian",
                    "Baczyński",
                    "PASSW)RD123"
            ),
            new User(
                    UUID.randomUUID(),
                    LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
                    "Leader@email.com",
                    "Michał",
                    "Bitnerowski",
                    "PASSW)RD123"
            )
    );

    Instant getDate(int dayOfMonth) {
        return LocalDate.of(2021, 6, 1 + dayOfMonth).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    TaskDetails taskDetailsEasy = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC), Difficulty.LOW);
    TaskDetails taskDetailsMedium = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
            Difficulty.MEDIUM);
    TaskDetails taskDetailsHard = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC), Difficulty.HIGH);

    Task task1 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(3), taskDetailsEasy);
    Task task2 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(1), taskDetailsEasy);
    Task task3 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(6), taskDetailsMedium);
    Task task4 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(4), taskDetailsMedium);
    Task task5 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(8), taskDetailsHard);
    Task task6 = new Task(
            UUID.randomUUID(), getDate(0), users.get(2), Status.DONE, getDate(5), taskDetailsHard);

    List<Task> tasks = List.of(task2, task1, task4, task3, task6, task5);

    void init() {

        task1.setAssignee(users.get(0));
        task3.setAssignee(users.get(0));
        task5.setAssignee(users.get(0));

        task2.setAssignee(users.get(1));
        task4.setAssignee(users.get(1));
        task6.setAssignee(users.get(1));

        missingUserEvaluator = new MissingUserEvaluator(usersEvaluator);
    }

    @Test
    void evaluate_AddsMissingMembers() {

        when(usersEvaluator.evaluate(any(), any())).thenReturn(List.of(
                new UserEvaluation(users.get(0), 6.0),
                new UserEvaluation(users.get(1), 10.0)
        ));

        init();

        var actual = missingUserEvaluator.evaluate(new HashSet<>(users), tasks);

        verify(usersEvaluator, Mockito.atMost(6)).evaluate(any(), any());

        assertThat(actual)
                .extracting(UserEvaluation::getScore)
                .containsExactly(10.0, 6.0, 0.0);

    }
}