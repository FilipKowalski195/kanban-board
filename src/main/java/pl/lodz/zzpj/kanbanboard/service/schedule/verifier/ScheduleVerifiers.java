package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import java.time.LocalDate;
import java.util.List;

public final class ScheduleVerifiers {
    private static final long LONG_PERIOD_THRESHOLD = 10;
    private static final long SHORT_PERIOD_THRESHOLD = 1;

    private ScheduleVerifiers() { }

    public static ScheduleVerifier holidays(List<LocalDate> holidays) {
        return new HolidaysVerifier(holidays);
    }

    public static ScheduleVerifier weekend() {
        return new WeekendVerifier();
    }

    public static ScheduleVerifier shortPeriod() {
        return new ShortPeriodVerifier(SHORT_PERIOD_THRESHOLD);
    }

    public static ScheduleVerifier longPeriod() {
        return new LongPeriodVerifier(LONG_PERIOD_THRESHOLD);
    }

    public static ScheduleVerifier longWeekendVerifier(List<LocalDate> holidays, boolean allowSoft) {
        return new LongWeekendVerifier(holidays, allowSoft);
    }

    public static ScheduleVerifier packDetached(List<LocalDate> holidays) {
        return PackedVerifier
                .init()
                .with(longWeekendVerifier(holidays, false));
    }

    public static ScheduleVerifier packLoose(List<LocalDate> holidays) {

        return PackedVerifier
                .init()
                .with(longWeekendVerifier(holidays, true));
    }

    public static ScheduleVerifier packNormal(List<LocalDate> holidays) {
        return PackedVerifier
                .init()
                .with(longPeriod())
                .with(shortPeriod())
                .with(holidays(holidays))
                .with(longWeekendVerifier(holidays, true));
    }

    public static ScheduleVerifier packDetailed(List<LocalDate> holidays) {
        return PackedVerifier
                .init()
                .with(weekend())
                .with(longPeriod())
                .with(shortPeriod())
                .with(holidays(holidays))
                .with(longWeekendVerifier(holidays, true));
    }
}
