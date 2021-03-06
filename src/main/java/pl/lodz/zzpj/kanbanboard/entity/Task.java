package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task extends Base {

    @Id
    @GeneratedValue
    private Long id;

    public enum Status {
        TODO, IN_PROGRESS, TO_REVIEW, DONE, CANCELED
    }

    @ManyToOne(optional = false)
    @NotNull
    private User creator;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    private User assignee;

    @Column
    private Instant closedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    @Valid
    private TaskDetails details;

    public Task() {
    }

    public Task(UUID uuid, Instant createdAt, User creator, TaskDetails details) {
        super(uuid, createdAt);
        this.creator = creator;
        this.status = Status.TODO;
        this.details = details;
    }

    public Task(
            UUID uuid,
            Instant createdAt,
            User creator,
            Status status,
            Instant closedAt,
            TaskDetails details
    ) {
        super(uuid, createdAt);
        this.creator = creator;
        this.status = status;
        this.closedAt = closedAt;
        this.details = details;
    }

    public boolean isAssigned() {
        return assignee != null;
    }

    public boolean isFinished() {
        return status == Status.DONE || status == Status.CANCELED;
    }

    public void closeTask(Instant now) {
        status = Status.DONE;
        closedAt = now;
    }

    public void cancelTask(Instant now) {
        status = Status.CANCELED;
        closedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public TaskDetails getDetails() {
        return details;
    }

    public void setDetails(TaskDetails details) {
        this.details = details;
    }


}
