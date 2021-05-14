package pl.lodz.zzpj.kanbanboard.dto.review;

import lombok.Value;

import java.util.UUID;

@Value
public class NewReviewDto {

    UUID taskUuid;

    String reviewerEmail;

    String comment;

    boolean rejected;
}
