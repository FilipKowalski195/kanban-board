package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import com.google.common.collect.Sets;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MissingUserEvaluator implements UsersEvaluator {

    private final UsersEvaluator base;

    public MissingUserEvaluator(UsersEvaluator base) {
        this.base = base;
    }

    @Override
    public List<UserEvaluation> evaluate(Set<User> members, List<Task> tasks) {
        var baseEval = base.evaluate(members, tasks);
        var evaluated = baseEval
                .stream()
                .map(UserEvaluation::getUser)
                .collect(Collectors.toSet());

        var missingMembers = Sets
                .difference(members, evaluated)
                .stream()
                .map(UserEvaluation::new)
                .collect(Collectors.toList());

        var result = new ArrayList<UserEvaluation>();

        result.addAll(baseEval);
        result.addAll(missingMembers);

        return result;
    }
}
