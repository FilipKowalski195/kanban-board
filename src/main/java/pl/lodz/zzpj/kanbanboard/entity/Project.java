package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "projects")
public class Project extends Base {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private User leader;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<User> members;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    private List<Task> tasks;

    public Project() {
    }

    public Project(UUID uuid, String name, Instant createdAt, User leader) {
        super(uuid, createdAt);
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.tasks = new ArrayList<>();
    }

    public Project(UUID uuid, Instant createdAt, String name, User leader, Set<User> members, List<Task> tasks) {
        super(uuid, createdAt);
        this.name = name;
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public User getLeader() {
        return leader;
    }

    public Set<User> getMembers() {
        return members;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
