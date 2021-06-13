package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.util.List;
import java.util.Set;

public interface UsersEvaluator {
    enum Metric {
        LAST_PERFORMANCE, PUNCTUALITY
    }

    List<UserEvaluation> evaluate(Set<User> members, List<Task> tasks);
}
