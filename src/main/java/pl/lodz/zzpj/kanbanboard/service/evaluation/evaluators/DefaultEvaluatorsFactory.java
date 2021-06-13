package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator.Metric;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task.LastPerformanceTaskEvaluator;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task.PunctualityTaskEvaluator;
import pl.lodz.zzpj.kanbanboard.utils.Composer;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.util.Map;

public class DefaultEvaluatorsFactory implements EvaluatorsFactory {

    private final DateProvider dateProvider;
    private final Map<Difficulty, Double> difficultyCoefficients;
    private final Map<Difficulty, Long> difficultyTimeEstimation;

    public DefaultEvaluatorsFactory(
            DateProvider dateProvider,
            Map<Difficulty, Double> difficultyCoefficients,
            Map<Difficulty, Long> difficultyTimeEstimation
    ) {
        this.dateProvider = dateProvider;
        this.difficultyCoefficients = difficultyCoefficients;
        this.difficultyTimeEstimation = difficultyTimeEstimation;
    }

    @Override
    public UsersEvaluator createEvaluator(Metric metric) {
        switch (metric) {
            case LAST_PERFORMANCE:
                return Composer
                        .startWith(new LastPerformanceTaskEvaluator(dateProvider, difficultyCoefficients))
                        .injectInto(SumBasedUsersEvaluator::new)
                        .injectInto(MissingUserEvaluator::new)
                        .compose();
            case PUNCTUALITY:
                return Composer
                        .startWith(new PunctualityTaskEvaluator(difficultyTimeEstimation))
                        .injectInto(SumBasedUsersEvaluator::new)
                        .injectInto(MissingUserEvaluator::new)
                        .compose();
            default:
                throw new IllegalArgumentException("Unsupported metric!");
        }
    }
}
