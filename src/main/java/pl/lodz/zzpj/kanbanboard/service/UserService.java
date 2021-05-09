package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.repositories.UsersRepository;

import java.util.List;

@Service
public class UserService {

    UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getUserByEmail(String email){
        return usersRepository.findUserByEmail(email);
    }

    public List<User> getAllUsers(){
        return usersRepository.findAll();
    }
}
