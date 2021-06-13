package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class PunctualityTaskEvaluator implements TaskEvaluator {

    private final Map<Difficulty, Long> difficultyTimeEstimations;

    public PunctualityTaskEvaluator(Map<Difficulty, Long> difficultyTimeEstimations) {
        this.difficultyTimeEstimations = difficultyTimeEstimations;
    }

    @Override
    public double evaluate(Task task) {
        checkNotNull(task.getClosedAt());

        var timespan = (double) ChronoUnit.DAYS.between(task.getCreatedAt(), task.getClosedAt());
        var estimation = (double) difficultyTimeEstimations.get(task.getDetails().getDifficulty());

        return (estimation - timespan) / timespan;
    }
}
