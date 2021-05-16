package pl.lodz.zzpj.kanbanboard.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.dto.UserDto;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.payload.request.LoginRequest;
import pl.lodz.zzpj.kanbanboard.payload.request.RegisterRequest;
import pl.lodz.zzpj.kanbanboard.payload.response.JwtResponse;
import pl.lodz.zzpj.kanbanboard.security.jwt.JwtTokenProvider;
import pl.lodz.zzpj.kanbanboard.security.user.UserDetailsImpl;
import pl.lodz.zzpj.kanbanboard.service.UserService;
import pl.lodz.zzpj.kanbanboard.service.converter.UserConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthResources {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthResources(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getUsername(),
                roles);
    }

    @PostMapping("/register")
    public UserDto registerUser(@RequestBody @Valid RegisterRequest userDto) throws BaseException {
        return UserConverter.toDto(userService.addUser(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword()
        ));
    }
}
