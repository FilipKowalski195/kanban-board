package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolidaysVerifier implements ScheduleVerifier {

    private final List<LocalDate> holidays;

    public HolidaysVerifier(List<LocalDate> holidays) {
        this.holidays = holidays;
    }

    @Override
    public List<ScheduleAlert> verify(LocalDate start, LocalDate end) {

        var days = Stream.iterate(start, it -> it.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .filter(holidays::contains)
                .collect(Collectors.toList());

        if (days.isEmpty()) {
            return List.of();
        }

        return List.of(ScheduleAlert.holidays(days));
    }


}
