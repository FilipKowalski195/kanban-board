package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.payload.request.RegisterRequest;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;

import java.time.Instant;
import java.util.UUID;

public final class UserConverter {
    public static User toDomain(RegisterRequest user, UUID uuid, Instant createdAt) {
        return new User(
                uuid,
                createdAt,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword()
        );
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getUuid(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    private UserConverter() {
    }
}
