package pl.lodz.zzpj.kanbanboard.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.zzpj.kanbanboard.entity.Role;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserFiller {

    public static void fillRepo(UsersRepository usersRepository, DateProvider dateProvider,PasswordEncoder passwordEncoder) throws BadOperationException {
        Set<Role> admin = new HashSet<>();
        admin.add(new Role(Role.ERole.LEADER));
        admin.add(new Role(Role.ERole.REVIEWER));
        admin.add(new Role(Role.ERole.USER));
        User adminEntity = new User(UUID.randomUUID(), dateProvider.now(), "admin@gmail.com", "Admin", "AdminLast",  passwordEncoder.encode("qwerty123"), admin);

        Set<Role> reviewier = new HashSet<>();
        reviewier.add(new Role(Role.ERole.USER));
        reviewier.add(new Role(Role.ERole.REVIEWER));
        User reviewierEntity = new User(UUID.randomUUID(), dateProvider.now(), "reviewer@gmail.com", "Reviewer", "ReviewerLast",  passwordEncoder.encode("qwerty123"), reviewier);

        Set<Role> user = new HashSet<>();
        user.add(new Role(Role.ERole.USER));
        User userEntity = new User(UUID.randomUUID(), dateProvider.now(), "user@gmail.com", "User", "UserLast", passwordEncoder.encode("qwerty123"), user);

        usersRepository.save(userEntity);

        usersRepository.save(reviewierEntity);

        usersRepository.save(adminEntity);
    }
}
