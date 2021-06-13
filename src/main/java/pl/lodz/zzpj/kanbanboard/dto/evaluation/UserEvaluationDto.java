package pl.lodz.zzpj.kanbanboard.dto.evaluation;

import lombok.Value;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UserEvaluation;

@Value
public class UserEvaluationDto {

    String email;
    double score;

    public UserEvaluationDto(UserEvaluation evaluation) {
        email = evaluation.getUser().getEmail();
        score = evaluation.getScore();
    }
}
