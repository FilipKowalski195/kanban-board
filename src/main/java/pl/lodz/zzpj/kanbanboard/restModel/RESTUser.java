package pl.lodz.zzpj.kanbanboard.restModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RESTUser {

    private String email;

    private String firstName;

    private String lastName;

    private String password;
}
