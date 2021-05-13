package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    private final DateProvider dateProvider;

    @Autowired
    public UserService(UsersRepository usersRepository, DateProvider dateProvider) {
        this.usersRepository = usersRepository;
        this.dateProvider = dateProvider;
    }

    public User getUserByEmail(String email) {
        return usersRepository.findUserByEmail(email).orElseThrow();
    }

    public List<User> getAllUsers(){
        return usersRepository.findAll();
    }

    public void addUser(UserDto userDto) {
        if(usersRepository.existsUserByEmail(userDto.getEmail())) {
            return;
        }

        var user = UserConverter.toDomain(userDto, UUID.randomUUID(), dateProvider.now());

        usersRepository.save(user);
    }
}
