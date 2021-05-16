package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.user.NewUserDto;
import pl.lodz.zzpj.kanbanboard.dto.user.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.UserService;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UsersResources {

    private final UserService userService;

    @Autowired
    public UsersResources(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<UserDto> getAllUsers() {
        return userService
                .getAll()
                .stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{email}")
    public UserDto getUserByEmail(@PathVariable String email) throws NotFoundException {
        var user = userService.getUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));

        return UserConverter.toDto(user);
    }

    @PostMapping("/user")
    public void addUser(@RequestBody @Valid NewUserDto userDto) throws BaseException {

        userService.add(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword()
        );
    }
}
