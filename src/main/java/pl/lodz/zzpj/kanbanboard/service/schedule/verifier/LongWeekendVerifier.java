package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import com.google.common.collect.Iterables;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LongWeekendVerifier implements ScheduleVerifier {

    private final List<LocalDate> holidays;
    private final boolean allowSoft;

    public LongWeekendVerifier(List<LocalDate> holidays, boolean allowSoft) {
        this.holidays = holidays;
        this.allowSoft = allowSoft;
    }

    @Override
    public List<ScheduleAlert> verify(LocalDate start, LocalDate end) {
        var days = Stream.iterate(start, it -> it.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.toList());

        var longWeekends = new ArrayList<List<LocalDate>>();
        var current = new ArrayList<LocalDate>();

        var workingDaysPeriod = 0;

        for (var day : days) {

            if (isFreeDay(day)) {
                workingDaysPeriod = -1;
            }

            if (workingDaysPeriod <= 0) {
                current.add(day);
            } else if (workingDaysPeriod == 1 && current.size() > 2) {
                clearLastIfFreeDay(current);
                if (current.size() > 2) {
                    longWeekends.add(new ArrayList<>(current));
                }
                current.clear();
            } else {
                current.clear();
            }

            workingDaysPeriod++;
        }

        return longWeekends
                .stream()
                .filter(this::filterSoftIfRequired)
                .map(this::mapToAlert)
                .collect(Collectors.toList());
    }

    private void clearLastIfFreeDay(List<LocalDate> current) {
        var last = Iterables.getLast(current);

        if (!isFreeDay(last)) {
            current.remove(last);
        }
    }

    private ScheduleAlert mapToAlert(List<LocalDate> week) {
        if (!allowSoft || week.stream().allMatch(this::isFreeDay)) {
            return ScheduleAlert.strongLongWeekend(week);
        } else {
            return ScheduleAlert.weakLongWeekend(week);
        }
    }

    private boolean filterSoftIfRequired(List<LocalDate> week) {
        return allowSoft || week.stream().allMatch(this::isFreeDay);
    }

    private boolean isFreeDay(LocalDate day) {
        return WeekendVerifier.WEEKEND_DAYS.contains(day.getDayOfWeek()) || holidays.contains(day);
    }
}
