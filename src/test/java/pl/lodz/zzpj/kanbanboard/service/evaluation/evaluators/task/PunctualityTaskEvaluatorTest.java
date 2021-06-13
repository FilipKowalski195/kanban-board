package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PunctualityTaskEvaluatorTest {

    PunctualityTaskEvaluator evaluator;

    User user = new User(
            UUID.randomUUID(),
            LocalDate.of(2020, 12, 12).atStartOfDay().toInstant(ZoneOffset.UTC),
            "User@email.com",
            "Filip",
            "Kowalski",
            "PASSW)RD123"
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

    Task easyLateTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(3), taskDetailsEasy);
    Task easyInTimeTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(1), taskDetailsEasy);
    Task mediumLateTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(6), taskDetailsMedium);
    Task mediumInTimeTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(4), taskDetailsMedium);
    Task hardLateTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(8), taskDetailsHard);
    Task hardInTimeTask = new Task(UUID.randomUUID(), getDate(0), user, Status.DONE, getDate(5), taskDetailsHard);

    Task notClosedTask = new Task(UUID.randomUUID(), getDate(0), user, Status.IN_PROGRESS, null, taskDetailsHard);

    void init() {
        evaluator = new PunctualityTaskEvaluator();
    }

    @Test
    void evaluate_sameLevelTasks_LateTasksHaveLessPoints() {

        init();

        var actualEasyLateTask = evaluator.evaluate(easyLateTask);
        var actualEasyInTimeTask = evaluator.evaluate(easyInTimeTask);

        assertThat(actualEasyInTimeTask).isGreaterThan(actualEasyLateTask);

        var actualMediumLateTask = evaluator.evaluate(mediumLateTask);
        var actualMediumInTimeTask = evaluator.evaluate(mediumInTimeTask);

        assertThat(actualMediumInTimeTask).isGreaterThan(actualMediumLateTask);

        var actualHardLateTask = evaluator.evaluate(hardLateTask);
        var actualHardInTimeTask = evaluator.evaluate(hardInTimeTask);

        assertThat(actualHardInTimeTask).isGreaterThan(actualHardLateTask);
    }

    @Test
    void evaluate_TaskIsNotClosed() {
        init();

        assertThatThrownBy(() -> evaluator.evaluate(notClosedTask))
                .isInstanceOf(NullPointerException.class);
    }

}