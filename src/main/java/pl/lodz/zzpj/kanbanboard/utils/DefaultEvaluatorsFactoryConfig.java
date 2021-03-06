package pl.lodz.zzpj.kanbanboard.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.DefaultEvaluatorsFactory;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.EvaluatorsFactory;

import java.util.Map;

@Configuration
public class DefaultEvaluatorsFactoryConfig {

    private final Map<Difficulty, Double> difficultyCoefficients = Map.of(
            Difficulty.LOW, 0.5,
            Difficulty.MEDIUM, 1.0,
            Difficulty.HIGH, 1.5
    );

    @Bean
    public EvaluatorsFactory defaultEvaluatorsFactory(
            DateProvider provider
    ) {
        return new DefaultEvaluatorsFactory(
                provider,
                difficultyCoefficients
        );
    }
}
