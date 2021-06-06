package pl.lodz.zzpj.kanbanboard.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.remote.HolidayApi;
import pl.lodz.zzpj.kanbanboard.remote.data.Holiday;
import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifier;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifiersFactory;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class ScheduleAssistantService {

    private final HolidayApi holidayApi;
    private final PackedVerifiersFactory factory;

    @Autowired
    public ScheduleAssistantService(
            HolidayApi holidayApi,
            PackedVerifiersFactory factory
    ) {
        this.holidayApi = holidayApi;
        this.factory = factory;
    }

    public List<ScheduleAlert> checkPeriod(LocalDate start, LocalDate end, PackedVerifier.Level level,  String country) {
        checkArgument(!end.isBefore(start), "Invalid period! Start date must be before end date!");

        var holidaysCalls = Stream.of(start.getYear(), end.getYear())
                .map(String::valueOf)
                .distinct()
                .map(year -> holidayApi.getHolidays(year, country))
                .map(mono -> mono.flux().flatMap(Flux::fromIterable))
                .collect(Collectors.toList());

        return Flux
                .merge(holidaysCalls)
                .collect(Collectors.toList())
                .map(holidays -> {
                    var verifier = factory.pack(level, mapHolidaysToDates(holidays));
                    return verifier.verify(start, end);
                }).block();
    }

    private static List<LocalDate> mapHolidaysToDates(List<Holiday> holidays) {
        return holidays
                .stream()
                .map(Holiday::getDate)
                .collect(Collectors.toList());
    }
}
