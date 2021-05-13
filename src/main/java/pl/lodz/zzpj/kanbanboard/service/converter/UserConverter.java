package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.time.Instant;
import java.util.UUID;

public final class UserConverter {
    public static User toDomain(UserDto user, UUID uuid, Instant createdAt) {
        return new User(
                uuid,
                createdAt,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword()
        );
    }

    private UserConverter() {
    }
}
