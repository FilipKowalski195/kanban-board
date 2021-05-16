package pl.lodz.zzpj.kanbanboard.dto;

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

    public UserDto(UUID uuid, Instant createdAt, String email, String firstName, String lastName) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
