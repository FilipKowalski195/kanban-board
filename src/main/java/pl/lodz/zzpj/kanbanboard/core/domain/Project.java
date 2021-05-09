package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

@ToString
public class Project extends Base {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private User leader;

    private final Set<User> members;

    private final List<Task> tasks;

    public Project(UUID uuid, Instant createdAt, User leader) {
        super(uuid, createdAt);
        this.leader = leader;
        this.members = new HashSet<>();
        this.tasks = new ArrayList<>();
    }

    public Project(UUID uuid, Instant createdAt, User leader, Set<User> members, List<Task> tasks) {
        super(uuid, createdAt);
        this.leader = leader;
        this.members = members;
        this.tasks = tasks;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(UUID uuid) {
        members.removeIf(u -> u.getUuid().equals(uuid));
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
