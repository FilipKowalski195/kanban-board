package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.Role;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;
import pl.lodz.zzpj.kanbanboard.utils.UserFiller;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends BaseService {

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UsersRepository usersRepository,
            DateProvider dateProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void fill() {
        UserFiller.fillRepo(usersRepository, dateProvider, passwordEncoder);
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));
    }

    public List<User> getAll() {
        return usersRepository.findAll();
    }

    public void add(String email, String firstName, String lastName, String password) throws BaseException {

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

//        user.getRoles().add(new Role(Role.USER));

        catchingValidation(() -> usersRepository.save(user));
    }

}
