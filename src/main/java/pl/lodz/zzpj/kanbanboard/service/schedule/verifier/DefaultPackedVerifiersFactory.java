package pl.lodz.zzpj.kanbanboard.service.schedule.verifier;

import org.springframework.stereotype.Component;
import pl.lodz.zzpj.kanbanboard.service.schedule.verifier.PackedVerifier.Level;

import java.time.LocalDate;
import java.util.List;

@Component
public class DefaultPackedVerifiersFactory implements PackedVerifiersFactory {
    @Override
    public ScheduleVerifier pack(Level level, List<LocalDate> holidays) {
        switch (level) {
            case DETAILED:
                return ScheduleVerifiers.packDetailed(holidays);
            case NORMAL:
                return ScheduleVerifiers.packNormal(holidays);
            case LOOSE:
                return ScheduleVerifiers.packLoose(holidays);
            case DETACHED:
                return ScheduleVerifiers.packDetached(holidays);
            default:
                throw new IllegalStateException();
        }
    }
}
