package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import org.assertj.core.data.Percentage;
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
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LastPerformanceTaskEvaluatorTest {

    @Mock
    DateProvider dateProvider;

    Map<Difficulty, Double> coefficientMap = Map.of(
            Difficulty.LOW, 1.0,
            Difficulty.MEDIUM, 2.0,
            Difficulty.HIGH, 3.0
    );

    LastPerformanceTaskEvaluator evaluator;

    Instant taskCreated = LocalDate.of(2021, 6, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant taskEnded = LocalDate.of(2021, 6, 5).atStartOfDay().toInstant(ZoneOffset.UTC);
    User user = new User(
            UUID.randomUUID(),
            LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
            "User@email.com",
            "Filip",
            "Kowalski",
            "PASSW)RD123"
    );

    TaskDetails taskDetailsEasy = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC), Difficulty.LOW);
    TaskDetails taskDetailsMedium = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
            Difficulty.MEDIUM);
    TaskDetails taskDetailsHard = new TaskDetails(
            "Name", "Description", LocalDate.of(2021, 6, 12).atStartOfDay().toInstant(ZoneOffset.UTC), Difficulty.HIGH);

    Task easyTask = new Task(UUID.randomUUID(), taskCreated, user, Status.DONE, taskEnded, taskDetailsEasy);
    Task mediumTask = new Task(UUID.randomUUID(), taskCreated, user, Status.DONE, taskEnded, taskDetailsMedium);
    Task hardTask = new Task(UUID.randomUUID(), taskCreated, user, Status.DONE, taskEnded, taskDetailsHard);
    Task notClosedTask = new Task(UUID.randomUUID(), taskCreated, user, Status.IN_PROGRESS, null, taskDetailsHard);

    void init() {
        evaluator = new LastPerformanceTaskEvaluator(dateProvider, coefficientMap);
    }

    @Test
    void evaluate_MoreDifficultTaskGivesMorePoints() {

        when(dateProvider.now())
                .thenReturn(LocalDate.of(2021, 6, 13).atStartOfDay().toInstant(ZoneOffset.UTC));

        init();

        var actualEasyTask = evaluator.evaluate(easyTask);
        var actualMediumTask = evaluator.evaluate(mediumTask);
        var actualHardTask = evaluator.evaluate(hardTask);

        assertThat(actualMediumTask).isGreaterThan(actualEasyTask);
        assertThat(actualHardTask).isGreaterThan(actualMediumTask);

        assertThat(actualEasyTask).isCloseTo(0.91, Percentage.withPercentage(1));
        assertThat(actualMediumTask).isCloseTo(1.82, Percentage.withPercentage(1));
        assertThat(actualHardTask).isCloseTo(2.73, Percentage.withPercentage(1));

        verify(dateProvider, Mockito.atMost(3)).now();
    }

    @Test
    void evaluate_TaskIsNotClosed() {

        init();

        assertThatThrownBy(() -> evaluator.evaluate(notClosedTask))
                .isInstanceOf(NullPointerException.class);

    }
}