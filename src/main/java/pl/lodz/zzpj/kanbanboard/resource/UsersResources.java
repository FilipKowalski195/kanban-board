package pl.lodz.zzpj.kanbanboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.service.UserService;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")

public class UsersResources {

    @Autowired
    private final UserService userService;

    @Autowired
    public UsersResources(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService
                .getAllUsers()
                .stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{mail}")
    public UserDto getUserByEmail(@PathVariable String mail) throws NotFoundException {
        return UserConverter.toDto(userService.getUserByEmail(mail));
    }
}
