package pl.lodz.zzpj.kanbanboard.dto.project;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class NewProjectDto {

    @NotBlank
    String name;

    @Email
    String leaderEmail;
}
