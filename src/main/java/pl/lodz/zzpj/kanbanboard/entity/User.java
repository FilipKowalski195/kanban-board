package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.Instant;
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

    public User() {
    }

    public User(UUID uuid, Instant createdAt, String email, String firstName, String lastName, String password) {
        super(uuid, createdAt);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
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
