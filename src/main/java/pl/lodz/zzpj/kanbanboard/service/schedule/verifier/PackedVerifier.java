package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import pl.lodz.zzpj.kanbanboard.service.schedule.advices.ScheduleAlert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PackedVerifier implements ScheduleVerifier {

    public enum Level {
        DETAILED, NORMAL, LOOSE, DETACHED
    }

    private final List<ScheduleVerifier> checkers = new ArrayList<>();

    private PackedVerifier() { }

    public static PackedVerifier init() {
        return new PackedVerifier();
    }

    public PackedVerifier with(ScheduleVerifier singleVerifier) {
        checkers.add(singleVerifier);
        return this;
    }

    @Override
    public List<ScheduleAlert> verify(LocalDate start, LocalDate end) {
        return checkers
                .stream()
                .map(ver -> ver.verify(start, end))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
