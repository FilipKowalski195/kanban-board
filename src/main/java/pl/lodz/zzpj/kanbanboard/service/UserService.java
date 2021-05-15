package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.ERole;
import pl.lodz.zzpj.kanbanboard.entity.Role;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService extends BaseService {

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersRepository usersRepository, DateProvider dateProvider) {
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
    }

    @PostConstruct
    public void fillRepo() throws BadOperationException {
        Set<Role> admin = new HashSet<>();
        admin.add(new Role(ERole.LEADER));
        admin.add(new Role(ERole.REVIEWER));
        admin.add(new Role(ERole.USER));
        User adminEntity = new User(UUID.randomUUID(), dateProvider.now(), "admin@gmail.com", "Admin", "AdminLast",  passwordEncoder.encode("qwerty123"), admin);

        Set<Role> reviewier = new HashSet<>();
        reviewier.add(new Role(ERole.USER));
        reviewier.add(new Role(ERole.REVIEWER));
        User reviewierEntity = new User(UUID.randomUUID(), dateProvider.now(), "reviewer@gmail.com", "Reviewer", "ReviewerLast",  passwordEncoder.encode("qwerty123"), reviewier);

        Set<Role> user = new HashSet<>();
        user.add(new Role(ERole.USER));
        User userEntity = new User(UUID.randomUUID(), dateProvider.now(), "user@gmail.com", "User", "UserLast", passwordEncoder.encode("qwerty123"), user);

        usersRepository.save(userEntity);

        usersRepository.save(reviewierEntity);

        usersRepository.save(adminEntity);
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }

    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    public User addUser(String email, String firstName, String lastName, String password) throws BaseException {

        if (usersRepository.existsUserByEmail(email)) {
            throw ConflictException.uniqueField(User.class, "email", email);
        }

        var user = new User(
                UUID.randomUUID(),
                dateProvider.now(),
                email,
                firstName,
                lastName,
                password
        );

        user.getRoles().add(new Role(ERole.USER));

        return catchingValidation(() -> usersRepository.save(user));
    }

}
