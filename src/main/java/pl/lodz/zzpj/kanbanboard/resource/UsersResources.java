package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.service.UserService;

import java.util.List;

@RestController
public class UsersResources {

    private final UserService userService;

    @Autowired
    public UsersResources(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/user/{mail}")
    public User getUserByEmail(@PathVariable String mail){
        return userService.getUserByEmail(mail);
    }

    @PostMapping("/user")
    public User addUser(@RequestBody UserDto user){

        userService.addUser(user);

        return userService.getUserByEmail(user.getEmail());
    }
}
