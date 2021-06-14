package pl.lodz.zzpj.kanbanboard.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lodz.zzpj.kanbanboard.entity.*;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataFiller {
    private final UsersRepository usersRepository;
    private final ProjectsRepository projectsRepository;
    private final DateProvider dateProvider;
    private final PasswordEncoder passwordEncoder;

    private User leader;

    private final List<Instant> datesPool = List.of(
            Instant.parse("2021-06-11T10:15:30.00Z"), // create
            Instant.parse("2021-06-12T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-12T10:15:30.00Z"),  // close

            Instant.parse("2021-06-10T10:15:30.00Z"), // create
            Instant.parse("2021-06-11T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-11T10:15:30.00Z"),  // close

            Instant.parse("2021-06-07T10:15:30.00Z"), // create
            Instant.parse("2021-06-11T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-11T10:15:30.00Z"),  // close

            Instant.parse("2021-06-08T10:15:30.00Z"), // create
            Instant.parse("2021-06-13T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-10T10:15:30.00Z"),  // close

            Instant.parse("2021-06-07T10:15:30.00Z"), // create
            Instant.parse("2021-06-13T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-09T10:15:30.00Z"),  // close

            Instant.parse("2021-06-06T10:15:30.00Z"), // create
            Instant.parse("2021-06-10T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-08T10:15:30.00Z"),  // close

            Instant.parse("2021-06-11T10:15:30.00Z"), // create
            Instant.parse("2021-06-12T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-14T10:15:30.00Z"),  // close

            Instant.parse("2021-06-05T10:15:30.00Z"), // create
            Instant.parse("2021-06-10T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-13T10:15:30.00Z"),  // close

            Instant.parse("2021-06-03T10:15:30.00Z"), // create
            Instant.parse("2021-06-06T10:20:30.00Z"), // deadline
            Instant.parse("2021-06-11T10:15:30.00Z")  // close
    );


    private final List<Difficulty> difficultiesPool = List.of(
            Difficulty.LOW,   // 0
            Difficulty.LOW,   // 1
            Difficulty.MEDIUM,// 2
            Difficulty.HIGH,  // 3
            Difficulty.MEDIUM,// 4
            Difficulty.HIGH,  // 5
            Difficulty.MEDIUM,// 6
            Difficulty.HIGH,  // 7
            Difficulty.HIGH,  // 8
            Difficulty.HIGH   // 9
    );

    @Autowired
    public DataFiller(
            UsersRepository usersRepository,
            ProjectsRepository projectsRepository,
            DateProvider dateProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.usersRepository = usersRepository;
        this.projectsRepository = projectsRepository;
        this.dateProvider = dateProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public void fillRepos() {

        var adminEntity = new User(
                UUID.randomUUID(),
                dateProvider.now(),
                "admin@gmail.com",
                "Admin",
                "AdminLast",
                passwordEncoder.encode("qwerty123"),
                Set.of(new Role(Role.ADMIN))
        );

        if (usersRepository.findUserByEmail("admin@gmail.com").isPresent()) {
            return;
        }

        usersRepository.save(adminEntity);

        var entities = Stream.of("a", "b", "c", "d", "e")
                .map(this::generateUser)
                .collect(Collectors.toList());
        leader = entities.get(0);
        usersRepository.saveAll(entities);

        var project = new Project(
                UUID.fromString("3edc60cd-3739-4fa9-81a6-4e3415a9500a"),
                "Project",
                dateProvider.now(),
                entities.get(0),
                new HashSet<>(entities),
                List.of(
                        genTask(entities.get(0), 0),
                        genTask(entities.get(0), 1),
                        genTask(entities.get(0), 2),

                        genTask(entities.get(1), 3),
                        genTask(entities.get(1), 4),
                        genTask(entities.get(1), 5),

                        genTask(entities.get(2), 6),
                        genTask(entities.get(2), 7),
                        genTask(entities.get(2), 8)
                )
        );

        projectsRepository.save(project);
    }

    private User generateUser(String name) {
        return new User(
                UUID.randomUUID(),
                dateProvider.now(),
                name + "@gmail.com",
                name, name,
                passwordEncoder.encode("qwerty123"),
                Set.of(new Role(Role.USER))
        );
    }

    private Task genTask(User user, int id) {
        var task = new Task(
                UUID.randomUUID(),
                datesPool.get(id * 3),
                leader,
                Status.DONE,
                datesPool.get(id * 3 + 2),
                new TaskDetails(
                      "Task:" + user.getEmail() + ":"  + id,
                      "Task:" + user.getEmail() + ":"  + id,
                        datesPool.get(id * 3 + 1),
                        difficultiesPool.get(id),
                        List.of(new Review(
                                UUID.randomUUID(),
                                dateProvider.now(),
                                leader,
                                "OK",
                                false
                        ))
                )
        );

        task.setAssignee(user);

        return task;
    }
}
