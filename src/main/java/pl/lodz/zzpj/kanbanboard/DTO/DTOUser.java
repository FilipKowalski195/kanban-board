package pl.lodz.zzpj.kanbanboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTOUser {

    private String email;

    private String firstName;

    private String lastName;

    private String password;
}
