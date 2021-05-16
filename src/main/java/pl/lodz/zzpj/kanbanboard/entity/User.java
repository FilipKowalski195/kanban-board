package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends Base {

    @Id
    @GeneratedValue
    private Long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(UUID uuid, Instant createdAt, String email, String firstName, String lastName, String password) {
        super(uuid, createdAt);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        roles.add(new Role(Role.ERole.USER));
    }

    public User(UUID uuid, Instant createdAt,  @Email String email, String firstName, String lastName, String password, Set<Role> roles) {
        super(uuid, createdAt);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    public User(
            UUID uuid, Instant createdAt, Long id, String email, String firstName, String lastName, String password,
            Set<Role> roles) {
        super(uuid, createdAt);
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
