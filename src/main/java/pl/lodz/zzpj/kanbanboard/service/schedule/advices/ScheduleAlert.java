package pl.lodz.zzpj.kanbanboard.service.schedule.advices;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Getter
@AllArgsConstructor
public class ScheduleAlert {

    public enum Type {
        WEEKEND, SHORT_PERIOD, LONG_PERIOD, HOLIDAYS, SOFT_LONG_WEEKEND, STRICT_LONG_WEEKEND
    }

    public enum Trigger {
        RANGE, DAYS
    }

    private final Type type;

    private final Set<LocalDate> trigger;

    private final Trigger triggerType;

    public static ScheduleAlert weekend(Collection<LocalDate> days) {
        return new ScheduleAlert(Type.WEEKEND, new TreeSet<>(days), Trigger.DAYS);
    }

    public static ScheduleAlert shortPeriod(Collection<LocalDate> days) {
        return new ScheduleAlert(Type.SHORT_PERIOD, new TreeSet<>(days), Trigger.RANGE);
    }

    public static ScheduleAlert longPeriod(Collection<LocalDate> days) {
        return new ScheduleAlert(Type.LONG_PERIOD, new TreeSet<>(days), Trigger.RANGE);
    }

    public static ScheduleAlert holidays(Collection<LocalDate> holidays) {
        return new ScheduleAlert(Type.HOLIDAYS, new TreeSet<>(holidays), Trigger.DAYS);
    }

    public static ScheduleAlert strongLongWeekend(Collection<LocalDate> days) {
        return new ScheduleAlert(Type.STRICT_LONG_WEEKEND, new TreeSet<>(days), Trigger.DAYS);
    }

    public static ScheduleAlert weakLongWeekend(Collection<LocalDate> days) {
        return new ScheduleAlert(Type.SOFT_LONG_WEEKEND, new TreeSet<>(days), Trigger.DAYS);
    }

}
