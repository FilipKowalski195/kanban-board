package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleVerifier {

    List<ScheduleAlert> verify(LocalDate start, LocalDate end);
}
