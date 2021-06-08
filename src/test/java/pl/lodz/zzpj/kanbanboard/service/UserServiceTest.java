package pl.lodz.zzpj.kanbanboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.zzpj.kanbanboard.entity.Role;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UsersRepository usersRepository;

    @Mock
    DateProvider dateProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;

    final String defaultEmail = "user@email.com";
    final String defaultFirstName = "John";
    final String defaultLastName = "Cena";
    final String defaultPassword = "P@ssw0rd3";
    final Role defaultRole = new Role(Role.USER);
    final Instant defaultInstant = Instant.now();

    final UUID userUuid = UUID.randomUUID();
    final User defaultUser = new User(userUuid, Instant.now(), defaultEmail, defaultFirstName, defaultLastName, defaultPassword);

    final String userNotExistEmail = "userNotExist@email.com";

    private void noMoreInteractions(){
        verifyNoMoreInteractions(usersRepository, dateProvider, passwordEncoder);
    }

    private void prepareUserService(){
        userService = new UserService(usersRepository, dateProvider, passwordEncoder);
    }

    @Test
    void getAll() {
        prepareUserService();

        userService.getAll();

        verify(usersRepository)
                .findAll();

        noMoreInteractions();
    }

    @Test
    void getUserByEmail_userDoesNotExist_ExceptionThrown(){
        when(usersRepository.findUserByEmail(userNotExistEmail))
                .thenReturn(Optional.empty());

        prepareUserService();

        assertThatThrownBy(() -> userService.getUserByEmail(userNotExistEmail))
                .isInstanceOf(NotFoundException.class);

        verify(usersRepository)
                .findUserByEmail(userNotExistEmail);

        noMoreInteractions();
    }

    @Test
    void getUserByEmail_userExists_userReturned() throws NotFoundException {
        when(usersRepository.findUserByEmail(defaultEmail))
                .thenReturn(Optional.of(defaultUser));

        prepareUserService();

        userService.getUserByEmail(defaultEmail);

        verify(usersRepository)
                .findUserByEmail(defaultEmail);

        noMoreInteractions();
    }

    @Test
    void add_userWithThisEmailExists_ExceptionThrown() {
        when(usersRepository.existsUserByEmail(defaultEmail))
                .thenReturn(true);

        prepareUserService();

        assertThatThrownBy(() -> userService.add(defaultEmail, defaultFirstName, defaultLastName, defaultPassword))
                .isInstanceOf(ConflictException.class);

        verify(usersRepository)
                .existsUserByEmail(defaultEmail);

        noMoreInteractions();
    }

    @Test
    void add_userDoesNotExist_userAdded() throws BaseException {
        when(usersRepository.existsUserByEmail(defaultEmail))
                .thenReturn(false);
        when(dateProvider.now())
                .thenReturn(defaultInstant);

        prepareUserService();

        userService.add(defaultEmail, defaultFirstName, defaultLastName, defaultPassword);

        ArgumentMatcher<User> userMatcher =
                (user) -> user.getEmail().equals(defaultEmail) &&
                    user.getFirstName().equals(defaultFirstName) &&
                    user.getLastName().equals(defaultLastName) &&
                    user.getPassword().equals(defaultPassword);

        verify(usersRepository)
                .existsUserByEmail(defaultEmail);
        verify(dateProvider)
                .now();
        verify(usersRepository)
                .save(ArgumentMatchers.argThat(userMatcher));

        noMoreInteractions();
    }
}
