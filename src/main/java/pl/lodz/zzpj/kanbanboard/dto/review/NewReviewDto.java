package pl.lodz.zzpj.kanbanboard.dto.review;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class NewReviewDto {

    @Email
    @NotBlank
    String reviewerEmail;

    String comment;

    @NotNull
    Boolean rejected;
}
