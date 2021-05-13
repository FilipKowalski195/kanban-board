package pl.lodz.zzpj.kanbanboard.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BasicDateProvider implements DateProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
