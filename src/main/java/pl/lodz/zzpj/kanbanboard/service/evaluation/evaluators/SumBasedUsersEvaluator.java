package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.task.TaskEvaluator;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class SumBasedUsersEvaluator implements UsersEvaluator {

    private final TaskEvaluator taskEvaluator;

    public SumBasedUsersEvaluator(TaskEvaluator taskEvaluator) {
        this.taskEvaluator = taskEvaluator;
    }

    @Override
    public List<UserEvaluation> evaluate(
            Set<User> members, List<Task> tasks
    ) {
        return tasks
                .stream()
                .collect(Collectors.groupingBy(Task::getAssignee))
                .entrySet()
                .stream()
                .map(this::evaluateTasks)
                .collect(Collectors.toList());
    }

    private UserEvaluation evaluateTasks(Entry<User, List<Task>> userTasks) {
        var score = userTasks
                .getValue()
                .stream()
                .mapToDouble(taskEvaluator::evaluate)
                .sum();

        return new UserEvaluation(userTasks.getKey(), score);
    }
}
