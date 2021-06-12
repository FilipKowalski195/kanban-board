package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackedVerifierTest {

    @Mock
    WeekendVerifier weekendVerifier;

    @Mock
    LongWeekendVerifier longWeekendVerifier;

    @Mock
    LongPeriodVerifier longPeriodVerifier;

    @Mock
    ShortPeriodVerifier shortPeriodVerifier;

    @Mock
    HolidaysVerifier holidaysVerifier;

    @Test
    void verify_returnSetOfSchedules() {

        var weekendList =  List.of(
                new ScheduleAlert(Type.WEEKEND,
                Set.of(LocalDate.of(2021, 8, 6)),
                Trigger.DAYS)
        );

        var longWeekendList = List.of(
                new ScheduleAlert(Type.STRICT_LONG_WEEKEND,
                Set.of(LocalDate.of(2021, 8, 6),
                LocalDate.of(2021, 8, 9)),
                Trigger.RANGE)
        );

        var longPeriodList = List.of(
                new ScheduleAlert(Type.LONG_PERIOD,
                Set.of(LocalDate.of(2021, 8, 6), LocalDate.of(2021, 8, 9)),
                Trigger.DAYS
                )
        );

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 15);

        when(weekendVerifier.verify(start, end)).thenReturn(weekendList);

        when(longWeekendVerifier.verify(start, end)).thenReturn(longWeekendList);

        when(longPeriodVerifier.verify(start, end)).thenReturn(longPeriodList);

        var actual = PackedVerifier.init()
                                            .with(weekendVerifier)
                                            .with(longWeekendVerifier)
                                            .with(longPeriodVerifier)
                                            .verify(start, end);

        assertThat(actual)
                .extracting(ScheduleAlert::getType)
                .containsExactly(Type.WEEKEND, Type.STRICT_LONG_WEEKEND, Type.LONG_PERIOD);

        assertThat(actual)
                .extracting(ScheduleAlert::getTriggerType)
                .containsExactly(Trigger.DAYS, Trigger.RANGE, Trigger.DAYS);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .containsExactly(weekendList.get(0).getTrigger(), longWeekendList.get(0).getTrigger(), longPeriodList.get(0).getTrigger());
    }
    @Test
    void verify_returnEmptySetOfSchedules() {

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 15);

        when(weekendVerifier.verify(start, end)).thenReturn(List.of());

        when(longWeekendVerifier.verify(start, end)).thenReturn(List.of());

        when(longPeriodVerifier.verify(start, end)).thenReturn(List.of());

        var actual = PackedVerifier.init()
                .with(weekendVerifier)
                .with(longWeekendVerifier)
                .with(longPeriodVerifier)
                .with(shortPeriodVerifier)
                .with(holidaysVerifier)
                .verify(start, end);

        assertThat(actual).isEmpty();
    }
}