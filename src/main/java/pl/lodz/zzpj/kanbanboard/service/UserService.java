package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.repositories.UsersRepository;
import pl.lodz.zzpj.kanbanboard.DTO.DTOUser;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getUserByEmail(String email) {
        return usersRepository.findUserByEmail(email);
    }

    public List<User> getAllUsers(){
        return usersRepository.findAll();
    }

    public void addUser(String email, String firstName, String lastName, String password) {
        if(usersRepository.existsUserByEmail(email)) {
            return;
        }
        Instant now = Instant.now();
        usersRepository.save(new User(UUID.randomUUID(), now, email, firstName, lastName, password));
    }

    public void addUser(DTOUser user) {
        if(usersRepository.existsUserByEmail(user.getEmail())) {
            return;
        }
        Instant now = Instant.now();
        usersRepository.save(new User(UUID.randomUUID(), now, user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword()));
    }
}
