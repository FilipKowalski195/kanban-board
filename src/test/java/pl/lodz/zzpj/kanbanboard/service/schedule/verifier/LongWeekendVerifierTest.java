package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
class LongWeekendVerifierTest {

    private final List<LocalDate> holidays = List.of(
            LocalDate.of(2021, 6, 11),
            LocalDate.of(2021, 7, 8)
    );

    @Test
    void verify_NoLongWeekend() {

        var verifier = new LongWeekendVerifier(holidays, true);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 8);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();
    }

    @Test
    void verify_StrictLongWeekend() {

        var verifier = new LongWeekendVerifier(holidays, false);

        var start = LocalDate.of(2021, 6, 9);
        var end = LocalDate.of(2021, 6, 15);

        var actual = verifier.verify(start, end);

        assertThat(actual).extracting(ScheduleAlert::getType).contains(Type.STRICT_LONG_WEEKEND);

        assertThat(actual).extracting(ScheduleAlert::getTriggerType).contains(Trigger.DAYS);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .containsExactly(Set.of(
                        LocalDate.of(2021, 6, 11),
                        LocalDate.of(2021, 6, 12),
                        LocalDate.of(2021, 6, 13)
                ));
    }

    @Test
    void verify_SoftLongWeekend_AllowOnlyStrict() {

        var verifier = new LongWeekendVerifier(holidays, false);

        var start = LocalDate.of(2021, 7, 5);
        var end = LocalDate.of(2021, 7, 15);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();

    }

    @Test
    void verify_SoftLongWeekend() {

        var verifier = new LongWeekendVerifier(holidays, true);

        var start = LocalDate.of(2021, 7, 5);
        var end = LocalDate.of(2021, 7, 15);

        var actual = verifier.verify(start, end);

        assertThat(actual)
                .extracting(ScheduleAlert::getType)
                .contains(Type.SOFT_LONG_WEEKEND);

        assertThat(actual)
                .extracting(ScheduleAlert::getTriggerType)
                .contains(Trigger.DAYS);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .containsExactly(Set.of(
                        LocalDate.of(2021, 7, 8),
                        LocalDate.of(2021, 7, 9),
                        LocalDate.of(2021, 7, 10),
                        LocalDate.of(2021, 7, 11)
                ));
    }
}