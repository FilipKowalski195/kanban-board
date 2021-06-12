package pl.lodz.zzpj.kanbanboard.service.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.remote.HolidayApi;
import pl.lodz.zzpj.kanbanboard.remote.data.Holiday;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifier.Level;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifiersFactory;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.ScheduleVerifier;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleAssistantServiceTest {

    @Mock
    HolidayApi holidayApi;

    @Mock
    PackedVerifiersFactory factory;

    private ScheduleAssistantService scheduleAssistantService;

    private final List<Holiday> holidays = List.of(
            new Holiday(
                    LocalDate.of(2020, 1, 2),
                    "3 Króli",
                    "3 Króli",
                    "PL",
                    true,
                    true,
                    "XD"
            ),
            new Holiday(
                    LocalDate.of(2020, 1, 5),
                    "3 Króli",
                    "3 Króli",
                    "PL",
                    true,
                    true,
                    "XD"
            )
    );

    void prepareAssistant() {
        scheduleAssistantService = new ScheduleAssistantService(holidayApi, factory);
    }

    private void noMoreInteractions() {
        verifyNoMoreInteractions(holidayApi, factory);
    }

    @Test
    void checkPeriod_endIsBeforeStart_ExceptionThrown() {

        prepareAssistant();

        var startDate = LocalDate.MAX;
        var endDate = LocalDate.MIN;

        assertThatThrownBy(() -> scheduleAssistantService.checkPeriod(startDate, endDate, Level.LOOSE, "PL"))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void checkPeriod_periodIsToWide_ExceptionThrown() {

        prepareAssistant();

        var startDate = LocalDate.of(2015, 12, 5);
        var endDate = LocalDate.of(2016, 2, 25);

        assertThatThrownBy(() -> scheduleAssistantService.checkPeriod(startDate, endDate, Level.LOOSE, "PL"))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void checkPeriod_sameYear() {

        var startDate = LocalDate.of(2018, 11, 15);
        var endDate = LocalDate.of(2018, 11, 20);
        var level = Level.NORMAL;
        var country = "PL";

        when(holidayApi.getHolidays(String.valueOf(startDate.getYear()), country)).thenReturn(
                Mono.just(List.of(holidays.get(0)))
        );

        var verifier = Mockito.mock(ScheduleVerifier.class);

        var alertList = List.of(ScheduleAlert.shortPeriod(Set.of(LocalDate.of(2018, 11, 17))));
        when(factory.pack(eq(level), any())).thenReturn(verifier);
        when(verifier.verify(any(), any())).thenReturn(alertList);

        prepareAssistant();

        var actual =  scheduleAssistantService.checkPeriod(startDate, endDate, level, country);

        assertThat(actual).isEqualTo(alertList);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        verify(holidayApi).getHolidays(argument.capture(), eq(country));

        var holidaysDates = Stream.of(holidays.get(0)).map(Holiday::getDate).collect(Collectors.toList());
        ArgumentMatcher<List<LocalDate>> holidaysMatcher = (list) -> list.containsAll(holidaysDates);
        verify(factory).pack(eq(level), ArgumentMatchers.argThat(holidaysMatcher));
        verify(verifier).verify(startDate, endDate);

        noMoreInteractions();

    }

    @Test
    void checkPeriod_differentYears() {

        var startDate = LocalDate.of(2019, 12, 30);
        var endDate = LocalDate.of(2020, 1, 15);
        var level = Level.NORMAL;
        var country = "PL";

        when(holidayApi.getHolidays(String.valueOf(startDate.getYear()), country)).thenReturn(
                Mono.just(List.of(holidays.get(0)))
        );

        when(holidayApi.getHolidays(String.valueOf(endDate.getYear()), country)).thenReturn(
                Mono.just(List.of(holidays.get(1)))
        );

        var verifier = Mockito.mock(ScheduleVerifier.class);


        when(factory.pack(eq(level), any())).thenReturn(verifier);
        when(verifier.verify(any(), any())).thenReturn(List.of());

        prepareAssistant();

        scheduleAssistantService.checkPeriod(startDate, endDate, level, country);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        verify(holidayApi, Mockito.times(2)).getHolidays(argument.capture(), eq(country));

        assertThat(argument.getAllValues()).contains("2019", "2020");

        var holidaysDates = holidays.stream().map(Holiday::getDate).collect(Collectors.toList());
        ArgumentMatcher<List<LocalDate>> holidaysMatcher = (list) -> list.containsAll(holidaysDates);
        verify(factory).pack(eq(level), ArgumentMatchers.argThat(holidaysMatcher));
        verify(verifier).verify(startDate, endDate);

        noMoreInteractions();
    }

}