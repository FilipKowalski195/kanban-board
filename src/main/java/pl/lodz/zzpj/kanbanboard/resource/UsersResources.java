package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.user.NewUserDto;
import pl.lodz.zzpj.kanbanboard.dto.user.UserDto;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.UserService;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UsersResources {

    private final UserService userService;

    @Autowired
    public UsersResources(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService
                .getAll()
                .stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{email}")
    public UserDto getUserByEmail(@PathVariable String email) throws NotFoundException {
        return UserConverter.toDto(userService.getUserByEmail(email));
    }

    @PostMapping
    public void addUser(@RequestBody @Valid NewUserDto userDto) throws BaseException {

        userService.add(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword()
        );
    }
}
