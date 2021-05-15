package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.NewUserDto;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.remote.HolidayApi;
import pl.lodz.zzpj.kanbanboard.remote.data.Holiday;
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
                .getAllUsers()
                .stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{mail}")
    public UserDto getUserByEmail(@PathVariable String mail) throws NotFoundException {
        return UserConverter.toDto(userService.getUserByEmail(mail));
    }

    @PostMapping("/user")
    public UserDto addUser(@RequestBody @Valid NewUserDto userDto) throws BaseException {

        var user = userService.addUser(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword()
        );

        return UserConverter.toDto(user);
    }

    @Autowired
    private HolidayApi api;

    @GetMapping("/test")
    public List<Holiday> test() {
        return api.getHolidays("2021", "PL").block();
    }
}
