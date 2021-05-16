package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.dto.user.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;

public final class UserConverter {
    private UserConverter() {
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
}
