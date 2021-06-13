package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class LastPerformanceTaskEvaluator implements TaskEvaluator {

    private final DateProvider dateProvider;
    private final Map<Difficulty, Double> coefficientMap;

    public LastPerformanceTaskEvaluator(
            DateProvider dateProvider,
            Map<Difficulty, Double> coefficientMap
    ) {
        checkArgument(
                coefficientMap.size() == Difficulty.values().length,
                "Coefficient map must cover all difficulty levels"
        );
        this.dateProvider = dateProvider;
        this.coefficientMap = coefficientMap;
    }

    private double timespanFunction(double x) {
        if (x > 27.0) {
            return 0.0;
        }

        return -Math.pow(x / 27.0, 2.0) + 1.0;
    }

    @Override
    public double evaluate(Task task) {
        checkNotNull(task.getClosedAt());

        var timespan = ChronoUnit.DAYS.between(task.getClosedAt(), dateProvider.now());
        var timespanModifier = timespanFunction(timespan);

        var diffModifier = coefficientMap.get(task.getDetails().getDifficulty());

        return diffModifier * timespanModifier;
    }
}
