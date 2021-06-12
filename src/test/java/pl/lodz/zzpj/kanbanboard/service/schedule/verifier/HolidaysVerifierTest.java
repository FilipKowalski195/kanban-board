package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class HolidaysVerifierTest {

    private final List<LocalDate> holidays = List.of(
            LocalDate.of(2021,6,11),
            LocalDate.of(2021,7,8)
    );

    @Test
    void verify_DoesNotContainHoliday() {

        var verifier = new HolidaysVerifier(holidays);

        var start = LocalDate.of(2021, 5, 6);
        var end = LocalDate.of(2021, 5, 8);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();
    }

    @Test
    void verify_ContainsHoliday() {

        var verifier = new HolidaysVerifier(holidays);

        var start = LocalDate.of(2021, 6, 5);
        var end = LocalDate.of(2021, 6, 15);

        var actual = verifier.verify(start, end);

        assertThat(actual)
                .extracting(ScheduleAlert::getType)
                .contains(Type.HOLIDAYS);

        assertThat(actual)
                .extracting(ScheduleAlert::getTriggerType)
                .contains(Trigger.DAYS);

        assertThat(actual)
                .extracting(ScheduleAlert::getTrigger)
                .contains(Set.of(LocalDate.of(2021,6,11)));
    }
}