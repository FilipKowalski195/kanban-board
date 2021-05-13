package pl.lodz.zzpj.kanbanboard.utils;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
