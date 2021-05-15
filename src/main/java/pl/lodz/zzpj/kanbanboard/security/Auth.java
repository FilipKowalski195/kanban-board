package pl.lodz.zzpj.kanbanboard.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.payload.request.LoginRequest;
import pl.lodz.zzpj.kanbanboard.payload.request.RegisterRequest;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.payload.response.JwtResponse;
import pl.lodz.zzpj.kanbanboard.payload.response.MessageResponse;
import pl.lodz.zzpj.kanbanboard.security.jwt.JwtTokenProvider;
import pl.lodz.zzpj.kanbanboard.security.user.UserDetailsImpl;
import pl.lodz.zzpj.kanbanboard.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class Auth {

    @Lazy
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterRequest userDto) throws BaseException {
        try{
            var user = userService.addUser(
                    userDto.getEmail(),
                    userDto.getFirstName(),
                    userDto.getLastName(),
                    userDto.getPassword()
            );
        }catch (BaseException e){
            return ResponseEntity.ok(new MessageResponse("Registration failure!"));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
