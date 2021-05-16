package pl.lodz.zzpj.kanbanboard.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 3, max = 32)
    private String firstName;
    
    @NotBlank
    @Size(min = 3, max = 32)
    private String lastName;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;


}
