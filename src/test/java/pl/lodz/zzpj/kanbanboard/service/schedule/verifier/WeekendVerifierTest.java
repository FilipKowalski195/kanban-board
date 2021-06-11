package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Trigger;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert.Type;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class WeekendVerifierTest {

    @Test
    void verify_notWeekend_returnsEmptyList() {

        var verifier = new WeekendVerifier();

        var start = LocalDate.of(2021, 6, 8);
        var end = LocalDate.of(2021, 6, 10);

        var actual = verifier.verify(start, end);

        assertThat(actual).isEmpty();

    }

    @Test
    void verify_WholeWeekend_returnsAlert() {

        var verifier = new WeekendVerifier();

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 12);

        var actual = verifier.verify(start, end);

        assertThat(actual).extracting(ScheduleAlert::getType).contains(Type.WEEKEND);

        assertThat(actual).extracting(ScheduleAlert::getTrigger).contains(Set.of(LocalDate.of(2021, 8, 7), LocalDate.of(2021, 8, 8)));

        assertThat(actual).extracting(ScheduleAlert::getTriggerType).containsExactly(Trigger.DAYS);
    }

    @Test
    void verify_OnlyOneDayOfWeekend_returnsAlert() {

        var verifier = new WeekendVerifier();

        var start = LocalDate.of(2021, 8, 6);
        var end = LocalDate.of(2021, 8, 7);

        var actual = verifier.verify(start, end);

        assertThat(actual).extracting(ScheduleAlert::getType).contains(Type.WEEKEND);

        assertThat(actual).extracting(ScheduleAlert::getTrigger).containsExactly(Set.of(LocalDate.of(2021, 8, 7)));

        assertThat(actual).extracting(ScheduleAlert::getTriggerType).containsExactly(Trigger.DAYS);

    }
}