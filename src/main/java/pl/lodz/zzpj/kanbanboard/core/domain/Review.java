package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.Value;

import java.time.Instant;

@Value
public class Review {

    User reviewer;

    String comment;

    Instant publishTime;

    boolean rejected;

}
