package pl.lodz.zzpj.kanbanboard.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.zzpj.kanbanboard.entity.Role;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;

import java.util.Set;
import java.util.UUID;

public final class UserFiller {

    private UserFiller() {
    }

    public static void fillRepo(
            UsersRepository usersRepository,
            DateProvider dateProvider,
            PasswordEncoder passwordEncoder
    ) {

        var adminEntity = new User(
                UUID.randomUUID(),
                dateProvider.now(),
                "admin@gmail.com",
                "Admin",
                "AdminLast",
                passwordEncoder.encode("qwerty123"),
                Set.of(new Role(Role.ADMIN))
        );

        var userEntity = new User(
                UUID.randomUUID(),
                dateProvider.now(),
                "user@gmail.com",
                "User",
                "UserLast",
                passwordEncoder.encode("qwerty123"),
                Set.of(new Role(Role.USER))
        );

        usersRepository.save(userEntity);

        usersRepository.save(adminEntity);
    }
}
