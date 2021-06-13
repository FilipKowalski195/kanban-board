package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import pl.lodz.zzpj.kanbanboard.entity.Task;

import java.time.temporal.ChronoUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class PunctualityTaskEvaluator implements TaskEvaluator {
    @Override
    public double evaluate(Task task) {
        checkNotNull(task.getClosedAt());

        var timespan = (double) ChronoUnit.DAYS.between(task.getCreatedAt(), task.getClosedAt());
        var estimation = (double) ChronoUnit.DAYS.between(task.getCreatedAt(), task.getDetails().getDeadLine());

        return (estimation - timespan) / timespan;
    }
}
