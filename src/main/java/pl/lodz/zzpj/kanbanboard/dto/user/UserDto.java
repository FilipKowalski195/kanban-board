package pl.lodz.zzpj.kanbanboard.dto.user;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class UserDto {
    UUID uuid;

    Instant createdAt;

    String email;

    String firstName;

    String lastName;
}
