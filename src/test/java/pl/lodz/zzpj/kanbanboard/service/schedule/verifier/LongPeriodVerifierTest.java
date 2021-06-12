package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LongPeriodVerifierTest {

    @Test
    void verify_periodIsShorter() {

        var verifier = new LongPeriodVerifier(5L);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 8);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();
    }

    @Test
    void verify_periodIsLonger() {

        var verifier = new LongPeriodVerifier(5L);

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 15);

        var actual = verifier.verify(start, end);

        assertThat(actual).extracting(ScheduleAlert::getType).contains(Type.LONG_PERIOD);

        assertThat(actual).extracting(ScheduleAlert::getTriggerType).contains(Trigger.RANGE);

        assertThat(actual).extracting(ScheduleAlert::getTrigger).containsExactly(Set.of(start, end));
    }

}