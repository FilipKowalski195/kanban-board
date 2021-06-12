package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
class ShortPeriodVerifierTest {

    @Test
    void verify_periodIsLonger() {

        var verifier = new ShortPeriodVerifier(5L);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 12);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();
    }

    @Test
    void verify_periodIsShorter() {

        var verifier = new ShortPeriodVerifier(5L);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 8);

        var actual = verifier.verify(start, end);

        assertThat(actual)
                .extracting(ScheduleAlert::getType)
                .contains(Type.SHORT_PERIOD);

        assertThat(actual)
                .extracting(ScheduleAlert::getTriggerType)
                .contains(Trigger.RANGE);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .containsExactly(Set.of(start, end));
    }

    @Test
    void verify_periodIsShorter_OneDayOnly() {

        var verifier = new ShortPeriodVerifier(5L);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 6);

        var actual = verifier.verify(start, end);

        assertThat(actual)
                .extracting(ScheduleAlert::getType)
                .contains(Type.SHORT_PERIOD);

        assertThat(actual)
                .extracting(ScheduleAlert::getTriggerType)
                .contains(Trigger.RANGE);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .containsExactly(Set.of(start));
    }
}