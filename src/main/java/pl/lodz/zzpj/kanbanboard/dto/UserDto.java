package pl.lodz.zzpj.kanbanboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private String email;

    private String firstName;

    private String lastName;

    private String password;
}
