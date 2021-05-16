package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import pl.lodz.zzpj.kanbanboard.dto.user.NewUserDto;
import pl.lodz.zzpj.kanbanboard.dto.user.UserDto;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.remote.HolidayApi;
import pl.lodz.zzpj.kanbanboard.remote.data.Holiday;
import pl.lodz.zzpj.kanbanboard.service.UserService;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UsersResources {

    private final UserService userService;

    @Autowired
    private HolidayApi api;
  
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

    @GetMapping("/{mail}")
    public UserDto getUserByEmail(@PathVariable String email) throws NotFoundException {
        var user = userService.getUserByEmail(email)
                .orElseThrow(() -> NotFoundException.notFound(User.class, "email", email));

        return UserConverter.toDto(user);
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

    @GetMapping("/test")
    public List<Holiday> test() {
        return api.getHolidays("2021", "PL").block();
    }
}
