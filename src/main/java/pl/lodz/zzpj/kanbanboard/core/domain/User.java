package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class User extends Base {

    private final String email;

    private String firstName;

    private String lastName;

    private String password;

    public User(UUID uuid, Instant createdAt, String email, String firstName, String lastName, String password) {
        super(uuid, createdAt);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
}
