package pl.lodz.zzpj.kanbanboard.dto.schedule;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;
import pl.lodz.zzpj.kanbanboard.utils.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Set;

@Value
public class ScheduleAdviceDto {
    Type type;

    @JsonSerialize(contentUsing = LocalDateSerializer.class)
    Set<LocalDate> trigger;

    Trigger triggerType;
}
