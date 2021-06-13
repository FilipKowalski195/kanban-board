package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.schedule.ScheduleAdviceDto;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.service.evaluation.UsersEvaluationService;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UserEvaluation;
import pl.lodz.zzpj.kanbanboard.service.evaluation.evaluators.UsersEvaluator.Metric;
import pl.lodz.zzpj.kanbanboard.service.schedule.ScheduleAssistantService;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AssistantResources {

    private final ScheduleAssistantService scheduleService;

    private final UsersEvaluationService evaluationService;

    public AssistantResources(
            ScheduleAssistantService scheduleService,
            UsersEvaluationService evaluationService
    ) {
        this.scheduleService = scheduleService;
        this.evaluationService = evaluationService;
    }

    @GetMapping("/assist/schedule")
    public List<ScheduleAdviceDto> checkSchedule(
            @RequestParam("start") @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = ISO.DATE) LocalDate end,
            @RequestParam("country") String country,
            @RequestParam("level") PackedVerifier.Level level
    ) {
        return scheduleService.checkPeriod(start, end, level, country)
                .stream()
                .map(ad -> new ScheduleAdviceDto(ad.getType(), ad.getTrigger(), ad.getTriggerType()))
                .collect(Collectors.toList());
    }

    @GetMapping("/projects/{projectId}/evaluate")
    public List<UserEvaluation> evaluate(
            @PathVariable UUID projectId,
            @RequestParam("metric") Metric metric
    ) throws BaseException {
        return evaluationService.evaluateProjectMembers(projectId, metric);
    }
}
