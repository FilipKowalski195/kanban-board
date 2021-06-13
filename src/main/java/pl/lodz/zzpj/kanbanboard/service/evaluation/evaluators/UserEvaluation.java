package pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators;

import lombok.Value;
import pl.lodz.zzpj.kanbanboard.entity.User;

@Value
public class UserEvaluation {
    User user;
    double score;

    public UserEvaluation(User user) {
        this.user = user;
        this.score = 0.0;
    }

    public UserEvaluation(User user, double score) {
        this.user = user;
        this.score = score;
    }
}
