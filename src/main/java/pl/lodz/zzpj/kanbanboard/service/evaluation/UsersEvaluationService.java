package pl.lodz.zzpj.kanbanboard.service.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TasksRepository;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.EvaluatorsFactory;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UserEvaluation;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator.Metric;

import java.util.List;
import java.util.UUID;

@Service
public class UsersEvaluationService {

    private final EvaluatorsFactory evalFactory;

    private final ProjectsRepository projectsRepository;

    private final TasksRepository tasksRepository;

    @Autowired
    public UsersEvaluationService(
            EvaluatorsFactory evalFactory,
            ProjectsRepository projectsRepository,
            TasksRepository tasksRepository
    ) {
        this.evalFactory = evalFactory;
        this.projectsRepository = projectsRepository;
        this.tasksRepository = tasksRepository;
    }

    public List<UserEvaluation> evaluateProjectMembers(UUID projectUuid, Metric metric) throws BaseException {

        var project = projectsRepository
                .findProjectByUuid(projectUuid)
                .orElseThrow(() -> NotFoundException.notFound(Project.class, "uuid", projectUuid));

        var evaluator = evalFactory.createEvaluator(metric);

        return evaluator.evaluate(
                project.getMembers(),
                tasksRepository.findAllByStatusEquals(Status.DONE)
        );
    }
}
