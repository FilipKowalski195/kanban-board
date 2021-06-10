package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.schedule.ScheduleAdviceDto;
import pl.lodz.zzpj.kanbanboard.service.schedule.ScheduleAssistantService;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AssistantResources {

    @Autowired
    private ScheduleAssistantService scheduleService;

    @PostMapping("/assist/schedule")
    public List<ScheduleAdviceDto> checkSchedule(
            @RequestParam("start") @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam("end")  @DateTimeFormat(iso = ISO.DATE) LocalDate end,
            @RequestParam("country") String country,
            @RequestParam("level") PackedVerifier.Level level
    ) {
        return scheduleService.checkPeriod(start, end, level, country)
                .stream()
                .map(ad -> new ScheduleAdviceDto(ad.getType(), ad.getTrigger(), ad.getTriggerType()))
                .collect(Collectors.toList());
    }
}
