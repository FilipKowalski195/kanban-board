package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task;

import pl.lodz.zzpj.kanbanboard.entity.Task;

@FunctionalInterface
public interface TaskEvaluator {
    double evaluate(Task task);
}
