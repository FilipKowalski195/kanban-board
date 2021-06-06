package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import com.google.common.collect.Lists;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeekendVerifier implements ScheduleVerifier {

    static final Set<DayOfWeek> WEEKEND_DAYS = Set.of(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY);

    WeekendVerifier() {

    }

    @Override
    public List<ScheduleAlert> verify(LocalDate start, LocalDate end) {
        var allAdvices = new ArrayList<ScheduleAlert>();
        var weekendDays = Stream.iterate(start, it -> it.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .filter(day -> WEEKEND_DAYS.contains(day.getDayOfWeek()))
                .collect(Collectors.toList());

        if (weekendDays.isEmpty()) {
            return List.of();
        }

        if (weekendDays.get(0).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            allAdvices.add(ScheduleAlert.weekend(Set.of(weekendDays.get(0))));
            weekendDays.remove(0);
        }

        var advices = Lists
                .partition(weekendDays, 2)
                .stream()
                .map(ScheduleAlert::weekend)
                .collect(Collectors.toList());

        allAdvices.addAll(advices);
        return allAdvices;
    }
}
