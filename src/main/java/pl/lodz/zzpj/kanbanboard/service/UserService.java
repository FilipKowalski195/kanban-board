package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.util.List;
import java.util.UUID;

@Service
public class UserService extends BaseService {

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    @Autowired
    public UserService(UsersRepository usersRepository, DateProvider dateProvider) {
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
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

        return catchingValidation(() -> usersRepository.save(user));
    }
}
