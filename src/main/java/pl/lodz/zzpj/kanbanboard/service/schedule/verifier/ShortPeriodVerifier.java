package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public class ShortPeriodVerifier implements ScheduleVerifier {

    private final long threshold;

    ShortPeriodVerifier(long threshold) {
        this.threshold = threshold;
    }

    @Override
    public List<ScheduleAlert> verify(LocalDate start, LocalDate end) {
        if (ChronoUnit.DAYS.between(start, end) <= threshold) {
            if (start.equals(end)) {
                return List.of(ScheduleAlert.shortPeriod(Set.of(start)));
            }
            return List.of(ScheduleAlert.shortPeriod(Set.of(start, end)));
        }
        return List.of();
    }
}

