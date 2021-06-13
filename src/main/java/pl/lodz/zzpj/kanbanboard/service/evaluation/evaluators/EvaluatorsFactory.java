package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator.Metric;

public interface EvaluatorsFactory {
    UsersEvaluator createEvaluator(Metric metric);
}
