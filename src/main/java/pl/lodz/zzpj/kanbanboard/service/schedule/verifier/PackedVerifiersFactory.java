package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import java.time.LocalDate;
import java.util.List;

public interface PackedVerifiersFactory {
    ScheduleVerifier pack(PackedVerifier.Level level, List<LocalDate> holidays);
}
