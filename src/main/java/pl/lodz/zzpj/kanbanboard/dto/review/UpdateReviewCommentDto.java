package pl.lodz.zzpj.kanbanboard.dto.review;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateReviewCommentDto {

    UUID reviewUuid;

    String comment;
}
